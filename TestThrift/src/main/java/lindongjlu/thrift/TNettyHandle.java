package lindongjlu.thrift;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

@SuppressWarnings("rawtypes")
public class TNettyHandle extends ChannelDuplexHandler implements TClientHandle {

	private final TProtocolFactory inProtoFactory;
	private final TProtocolFactory outProtoFactory;
	
	public TNettyHandle(TProtocolFactory inProtoFactory, TProtocolFactory outProtoFactory) {
		this.inProtoFactory = inProtoFactory;
		this.outProtoFactory = outProtoFactory;
	}
	
	public TNettyHandle(TProtocolFactory protoFactory) {
		this(protoFactory, protoFactory);
	}
	
	private Channel channel;
	private ByteBufTransport inTransport;
	private TProtocol inProtocol;
	private ByteBufTransport outTransport;
	private TProtocol outProtocol;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.channel = ctx.channel();
		this.inTransport = new ByteBufTransport();
		this.inProtocol = inProtoFactory.getProtocol(this.inTransport);
		this.outTransport = new ByteBufTransport();
		this.outProtocol = outProtoFactory.getProtocol(this.outTransport);
	}
	
	private static class MsgRequest {
		String methodName;
		TBase args;
		TBase result;
		SettableFuture future;
	}
	
	@Override
	public <T extends TBase<?, ?>> ListenableFuture<T> callBase(String methodName, TBase<?, ?> args, T result) {
		
		if (channel == null) {
			throw new NullPointerException("channel is null");
		}
		
		SettableFuture<T> future = SettableFuture.create();
		
		MsgRequest request = new MsgRequest();
		request.methodName = methodName;
		request.args = args;
		request.result = result;
		request.future = future;
		
		channel.writeAndFlush(request);
		
		return future;
	}
	
	class Session {
		TBase result;
		SettableFuture<TBase> future;
	}
	
	private int sessionSeq = 0;
	private Map<Integer, Session> sessionMap = new HashMap<Integer, Session>();
	
	@SuppressWarnings("unchecked")
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		
		if (!(msg instanceof MsgRequest))
		{
			return;
		}
		
		MsgRequest request = (MsgRequest)msg;
		
		int seqId = sessionSeq++;
		
		Session session = new Session();
		session.result = request.result;
		session.future = request.future;
		
		sessionMap.put(seqId, session);
		
		ByteBuf byteBuf = ctx.alloc().ioBuffer();
		int pos = byteBuf.writerIndex();
		byteBuf.writeInt(-1);
		
		inTransport.setByteBuf(byteBuf);
		
		inProtocol.writeMessageBegin(new TMessage(request.methodName, TMessageType.CALL, seqId));
		request.args.write(inProtocol);
		inProtocol.writeMessageEnd();
		inProtocol.getTransport().flush();
		
		inTransport.setByteBuf(null);
		
		byteBuf.setInt(pos, byteBuf.writerIndex() - pos - 4);
		
		ctx.write(byteBuf);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (!(msg instanceof ByteBuf))
		{
			return;
		}
		
		ByteBuf byteBuf = (ByteBuf) msg;
		
		if (byteBuf.readableBytes() < 4)
		{
			return;
		}
		
		int size = byteBuf.getInt(byteBuf.readerIndex());
		if (byteBuf.readableBytes() < size + 4)
		{
			return;
		}
		byteBuf.readInt();
		
		outTransport.setByteBuf(byteBuf);
		
		TMessage tmsg = null;
		try {
			tmsg = outProtocol.readMessageBegin();
		} catch (TException e) {
			// TODO e.printStackTrace();
			outTransport.setByteBuf(null);
			return;
		}
			
		Session session = sessionMap.remove(tmsg.seqid);
		if (session == null) {
			// TODO print error log
		} else {
			try {
				if (tmsg.type == TMessageType.EXCEPTION) {
					session.future.setException(TApplicationException.read(outProtocol));
				} else {
					session.result.read(outProtocol);
					session.future.set(session.result);
				}
				outProtocol.readMessageEnd();
			} catch (TException e) {
				session.future.setException(e);
			}
		}
		
		inTransport.setByteBuf(null);
	}
	
	private static class ByteBufTransport extends TTransport {

		public ByteBufTransport() {
		}
		
		private ByteBuf byteBuf;
		
		public void setByteBuf(ByteBuf byteBuf) {
			this.byteBuf = byteBuf;
		}
		
		@Override
		public boolean isOpen() {
			return true;
		}

		@Override
		public void open() throws TTransportException {
			/* Do nothing */
		}

		@Override
		public void close() {
			/* Do nothing */
		}

		@Override
		public int read(byte[] buf, int off, int len) throws TTransportException {
			int readableBytes = byteBuf.readableBytes();
			int amtToRead = len > readableBytes ? readableBytes : len;
			if (amtToRead > 0) {
				byteBuf.readBytes(buf, off, len);
				return amtToRead;
			}
			return 0;
		}

		@Override
		public void write(byte[] buf, int off, int len) throws TTransportException {
			byteBuf.writeBytes(buf, off, len);
		}

	}
	
}
