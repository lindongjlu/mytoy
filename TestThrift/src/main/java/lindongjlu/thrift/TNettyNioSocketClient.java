package lindongjlu.thrift;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

public class TNettyNioSocketClient implements
		TBaseClient<TNettyNioSocketClient> {

	private final NioEventLoopGroup eventLoop;
	private final String host;
	private final int port;

	private final NioSocketChannel channel;

	public TNettyNioSocketClient(final TProtocolFactory inputProtocolFactory,
			final TProtocolFactory outputProtocolFactory,
			NioEventLoopGroup eventLoop, String host, int port) {

		this.eventLoop = eventLoop;
		this.host = host;
		this.port = port;

		this.channel = new NioSocketChannel();

		this.channel.config().setTcpNoDelay(true);
		this.channel.pipeline().addLast(
				new ChannelInitializer<NioSocketChannel>() {

					@Override
					protected void initChannel(NioSocketChannel ch)
							throws Exception {
						ch.pipeline().addLast(
								new ChannelAdapter(inputProtocolFactory,
										outputProtocolFactory));
					}
				});
	}

	@Override
	public boolean isOpen() {
		return channel.isActive();
	}

	@Override
	public ListenableFuture<TNettyNioSocketClient> open() {
		try {
			final SettableFuture<TNettyNioSocketClient> openFuture = SettableFuture
					.create();
			this.eventLoop.register(this.channel).addListener(
					new GenericFutureListener<Future<Void>>() {

						@Override
						public void operationComplete(Future<Void> future)
								throws Exception {
							try {
								if (future.isSuccess()) {

									TNettyNioSocketClient.this.channel
											.connect(
													new InetSocketAddress(host,
															port))
											.addListener(
													new GenericFutureListener<Future<Void>>() {

														@Override
														public void operationComplete(
																Future<Void> future)
																throws Exception {
															if (future
																	.isSuccess()) {
																openFuture
																		.set(TNettyNioSocketClient.this);
															} else {
																openFuture
																		.setException(new TTransportException(
																				TTransportException.NOT_OPEN,
																				future.cause()));
															}
														}

													});

								} else {
									openFuture
											.setException(new TTransportException(
													TTransportException.NOT_OPEN,
													future.cause()));
								}
							} catch (Throwable th) {
								openFuture
										.setException(new TTransportException(
												TTransportException.NOT_OPEN,
												th));
							}
						}
					});
			return openFuture;
		} catch (Throwable th) {
			return Futures.immediateFailedFuture(new TTransportException(
					TTransportException.NOT_OPEN, th));
		}
	}

	@Override
	public ListenableFuture<TNettyNioSocketClient> close() {
		try {
			final SettableFuture<TNettyNioSocketClient> closeFuture = SettableFuture
					.create();
			this.channel.close().addListener(
					new GenericFutureListener<Future<Void>>() {

						@Override
						public void operationComplete(Future<Void> future)
								throws Exception {
							if (future.isSuccess()) {
								closeFuture.set(TNettyNioSocketClient.this);
							} else {
								closeFuture.setException(future.cause());
							}
						}
					});
			return closeFuture;
		} catch (Throwable th) {
			return Futures.immediateFailedFuture(th);
		}
	}

	@Override
	public <R extends TBase<?, ?>> ListenableFuture<R> callBase(
			String methodName, TBase<?, ?> args, R result) {
		if (!isOpen()) {
			return Futures.immediateFailedFuture(new TTransportException(
					TTransportException.NOT_OPEN, "client is not open"));
		}

		try {
			SettableFuture<R> callFuture = SettableFuture.create();
			channel.writeAndFlush(new RequestMsg(methodName, args, result,
					callFuture));
			return callFuture;
		} catch (Throwable th) {
			return Futures.immediateFailedFuture(new TTransportException(
					TTransportException.UNKNOWN, th));
		}
	}

	@SuppressWarnings("rawtypes")
	private static class RequestMsg {
		String methodName;
		TBase args;
		TBase result;
		SettableFuture future;

		RequestMsg(String methodName, TBase args, TBase result,
				SettableFuture future) {
			this.methodName = methodName;
			this.args = args;
			this.result = result;
			this.future = future;
		}
	}

	@SuppressWarnings("rawtypes")
	private static class CallSession {
		TBase result;
		SettableFuture future;

		CallSession(TBase result, SettableFuture future) {
			this.result = result;
			this.future = future;
		}
	}

	@SuppressWarnings("unchecked")
	private static class ChannelAdapter extends ChannelDuplexHandler {

		private final ByteBufTransport inputTransport;
		private final ByteBufTransport outputTransport;
		private final TProtocol inputProtocol;
		private final TProtocol outputProtocol;

		ChannelAdapter(TProtocolFactory inputProtocolFactory,
				TProtocolFactory outputProtocolFactory) {
			this.inputTransport = new ByteBufTransport();
			this.outputTransport = new ByteBufTransport();
			this.inputProtocol = inputProtocolFactory
					.getProtocol(this.inputTransport);
			this.outputProtocol = outputProtocolFactory
					.getProtocol(this.outputTransport);
		}

		private int callIdGenerator = 0;
		private Map<Integer, CallSession> sessionMap = new HashMap<Integer, CallSession>();

		@Override
		public void write(ChannelHandlerContext ctx, Object msg,
				ChannelPromise promise) throws Exception {

			if (!(msg instanceof RequestMsg)) {
				return;
			}

			RequestMsg request = (RequestMsg) msg;

			int callId = callIdGenerator++;
			while (sessionMap.containsKey(callId)) {
				callId = callIdGenerator++;
			}

			sessionMap.put(callId, new CallSession(request.result,
					request.future));

			try {

				ByteBuf byteBuf = ctx.alloc().ioBuffer();
				int pos = byteBuf.writerIndex();
				byteBuf.writeInt(-1);

				inputTransport.setByteBuf(byteBuf);

				inputProtocol.writeMessageBegin(new TMessage(
						request.methodName, TMessageType.CALL, callId));
				request.args.write(inputProtocol);
				inputProtocol.writeMessageEnd();
				inputProtocol.getTransport().flush();

				inputTransport.setByteBuf(null);

				byteBuf.setInt(pos, byteBuf.writerIndex() - pos - 4);

				ctx.write(byteBuf);

			} catch (TException ex) {
				sessionMap.remove(callId);
				request.future.setException(ex);
			}
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {

			if (!(msg instanceof ByteBuf)) {
				return;
			}

			ByteBuf byteBuf = (ByteBuf) msg;

			while (true) {

				if (byteBuf.readableBytes() < 4) {
					return;
				}

				int size = byteBuf.getInt(byteBuf.readerIndex());
				if (byteBuf.readableBytes() < size + 4) {
					return;
				}
				byteBuf.readInt();

				outputTransport.setByteBuf(byteBuf);

				TMessage tmsg = null;
				try {
					tmsg = outputProtocol.readMessageBegin();
				} catch (TException e) {
					// data is corrupted, close channel
					outputTransport.setByteBuf(null);
					ctx.close();
					return;
				}

				CallSession session = sessionMap.remove(tmsg.seqid);
				if (session == null) {
					// data is corrupted, close channel
					ctx.close();
					outputTransport.setByteBuf(null);
					return;
				}

				try {
					if (tmsg.type == TMessageType.EXCEPTION) {
						session.future.setException(TApplicationException
								.read(outputProtocol));
					} else {
						session.result.read(outputProtocol);
						session.future.set(session.result);
					}
					outputProtocol.readMessageEnd();
				} catch (TException e) {
					session.future.setException(e);
				}
				outputTransport.setByteBuf(null);
			}
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			if (!sessionMap.isEmpty()) {
				TTransportException ex = new TTransportException(
						TTransportException.END_OF_FILE, "channel is inactive!");
				for (Entry<Integer, CallSession> entry : sessionMap.entrySet()) {
					entry.getValue().future.setException(ex);
				}
				sessionMap.clear();
			}
		}

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
		public int read(byte[] buf, int off, int len)
				throws TTransportException {
			int readableBytes = byteBuf.readableBytes();
			int amtToRead = len > readableBytes ? readableBytes : len;
			if (amtToRead > 0) {
				byteBuf.readBytes(buf, off, len);
				return amtToRead;
			}
			return 0;
		}

		@Override
		public void write(byte[] buf, int off, int len)
				throws TTransportException {
			byteBuf.writeBytes(buf, off, len);
		}

	}

}
