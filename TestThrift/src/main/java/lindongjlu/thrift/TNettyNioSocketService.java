package lindongjlu.thrift;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Service;

public class TNettyNioSocketService<I> extends AbstractService implements Service {

	private final TBaseProcessor<I> processor;
	private final TServiceImplFactory<I> serviceImplFactory;
	private final Executor executor;
	private final TProtocolFactory inputProtocolFactory;
	private final TProtocolFactory outputProtocolFactory;
	
	private final NioEventLoopGroup bossGroup;
	private final NioEventLoopGroup workerGroup;
	
	private final String host;
	private final int port;
	
	private final NioServerSocketChannel channel;
	
	public TNettyNioSocketService(
			TBaseProcessor<I> processor,
			TServiceImplFactory<I> serviceImplFactory,
			Executor executor,
			TProtocolFactory inputProtocolFactory,
			TProtocolFactory outputProtocolFactory,
			NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup, 
			String host, int port) {
		this.processor = processor;
		this.serviceImplFactory = serviceImplFactory;
		this.executor = executor;
		this.inputProtocolFactory = inputProtocolFactory;
		this.outputProtocolFactory = outputProtocolFactory;
		
		this.bossGroup = bossGroup;
		this.workerGroup = workerGroup;
		
		this.host = host;
		this.port = port;
		
		this.channel = new NioServerSocketChannel();
		this.channel.config().setBacklog(100);
		this.channel.pipeline().addLast(new Acceptor());
	}
	
	@Override
	protected void doStart() {
		this.bossGroup.register(this.channel).addListener(new GenericFutureListener<Future<Void>>() {

			@Override
			public void operationComplete(Future<Void> future) throws Exception {
				if (future.isSuccess()) {
					TNettyNioSocketService.this.channel.bind(new InetSocketAddress(host, port)).addListener(new GenericFutureListener<Future<Void>>() {

						@Override
						public void operationComplete(Future<Void> future)
								throws Exception {
							if (future.isSuccess()) {
								TNettyNioSocketService.this.notifyStarted();
							} else {
								TNettyNioSocketService.this.notifyFailed(future.cause());
							}
						}
						
					});
				} else {
					TNettyNioSocketService.this.notifyFailed(future.cause());
				}
			}
			
		});
	}

	@Override
	protected void doStop() {
		this.channel.close().addListener(new GenericFutureListener<Future<Void>>() {

			@Override
			public void operationComplete(Future<Void> future)
					throws Exception {
				if (future.isSuccess()) {
					TNettyNioSocketService.this.notifyStopped();
				} else {
					TNettyNioSocketService.this.notifyFailed(future.cause());
				}
			}
			
		});
	}
	
	private class Acceptor extends ChannelInboundHandlerAdapter {
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			final Channel child = (Channel) msg;
			
			executor.execute(new Runnable() {

				@Override
				public void run() {
					// set option
					// add handler
					TBaseService<I> baseService = serviceImplFactory.getServiceImpl();
					baseService.initialize();
					child.pipeline().addLast(new ChannelAdapter(baseService));
					
					try {
//						System.out.println("start to sleep !");
//						TimeUnit.SECONDS.sleep(5);
//						System.out.println("finish to sleep !");
						workerGroup.register(child).addListener(new ChannelFutureListener() {
							@Override
							public void operationComplete(ChannelFuture future)
									throws Exception {
								if (!future.isSuccess()) {
									child.unsafe().closeForcibly();
								}
							}
						});
					} catch (Throwable t) {
						child.unsafe().closeForcibly();
					}
				}
				
			});
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	private static class ResponseMsg {

		int seqId;
		String methodName;
		TBase result;
		TApplicationException exception;

		public ResponseMsg(int seqId, String methodName, TBase result) {
			this.seqId = seqId;
			this.methodName = methodName;
			this.result = result;
			this.exception = null;
		}
		
		public ResponseMsg(int seqId, String methodName, TApplicationException exception) {
			this.seqId = seqId;
			this.methodName = methodName;
			this.exception = exception;
			this.result = null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private class ChannelAdapter extends ChannelDuplexHandler {

		private final TBaseService<I> baseService;
		private final ByteBufTransport inputTransport = new ByteBufTransport();
		private final ByteBufTransport outputTransport = new ByteBufTransport();
		private final TProtocol inputProtocol = inputProtocolFactory.getProtocol(this.inputTransport);
		private final TProtocol outputProtocol = outputProtocolFactory.getProtocol(this.outputTransport);

		public ChannelAdapter(TBaseService<I> baseService) {
			this.baseService = baseService;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {

			if (!(msg instanceof ByteBuf)) {
				return;
			}

			ByteBuf recvBuf = (ByteBuf) msg;

			while (true) {

				if (recvBuf.readableBytes() < 4) {
					return;
				}

				int size = recvBuf.getInt(recvBuf.readerIndex());
				if (recvBuf.readableBytes() < size + 4) {
					return;
				}

				inputTransport.setByteBuf(recvBuf.slice(recvBuf.readerIndex() + 4, size));
				recvBuf.skipBytes(size + 4);

				final TMessage tmsg;
				try {
					tmsg = inputProtocol.readMessageBegin();
				} catch (TException e) {
					// data may be corrupted
					inputTransport.setByteBuf(null);
					continue;
				}

				final TProcessFunction fn = processor.getProccessFunction(tmsg.name);
				if (fn == null) {
					ctx.channel().writeAndFlush(
							new ResponseMsg(tmsg.seqid, tmsg.name, 
									new TApplicationException(TApplicationException.UNKNOWN_METHOD, "Invalid method name: '" + tmsg.name + "'")));
					
					// data may be corrupted
					inputTransport.setByteBuf(null);
					continue;
				}
				
				// get empty args
				final TBase args = fn.getEmptyArgsInstance();
				try {
					args.read(inputProtocol);
				} catch (TProtocolException e) {
					
					ctx.channel().writeAndFlush(
							new ResponseMsg(tmsg.seqid, tmsg.name, 
									new TApplicationException(TApplicationException.PROTOCOL_ERROR, e.getMessage())));
					
					// data may be corrupted
					inputTransport.setByteBuf(null);
					continue;
				}
				inputProtocol.readMessageEnd();
				inputTransport.setByteBuf(null);
				
				final Channel ch = ctx.channel();
				executor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							ListenableFuture<TBase> future = fn.process(baseService.getService(), args);
							
							if (!fn.isOneway()) {
								Futures.addCallback(future, new FutureCallback<TBase>() {

									@Override
									public void onSuccess(TBase result) {
										ch.writeAndFlush(new ResponseMsg(tmsg.seqid, tmsg.name, result));
									}

									@Override
									public void onFailure(Throwable t) {
										ch.writeAndFlush(new ResponseMsg(tmsg.seqid, tmsg.name, new TApplicationException(TApplicationException.INTERNAL_ERROR, t.getMessage())));
									}
									
								});
							}
						} catch (Throwable t) {
							ch.writeAndFlush(new ResponseMsg(tmsg.seqid, tmsg.name, new TApplicationException(TApplicationException.INTERNAL_ERROR, t.getMessage())));
						}
					}
					
				});
			}
		}
		
		@Override
		public void write(ChannelHandlerContext ctx, Object msg,
				ChannelPromise promise) throws Exception {

			if (!(msg instanceof ResponseMsg)) {
				return;
			}

			ResponseMsg response = (ResponseMsg) msg;

			try {

				ByteBuf byteBuf = ctx.alloc().ioBuffer();
				int pos = byteBuf.writerIndex();
				byteBuf.writeInt(-1);

				outputTransport.setByteBuf(byteBuf);

				if (response.result != null) {
					outputProtocol.writeMessageBegin(new TMessage(
							response.methodName, TMessageType.REPLY, response.seqId));
					response.result.write(outputProtocol);
					outputProtocol.writeMessageEnd();
				} else {
					outputProtocol.writeMessageBegin(new TMessage(
							response.methodName, TMessageType.EXCEPTION, response.seqId));
					response.exception.write(outputProtocol);
					outputProtocol.writeMessageEnd();
				}
				
				outputProtocol.getTransport().flush();
				outputTransport.setByteBuf(null);

				byteBuf.setInt(pos, byteBuf.writerIndex() - pos - 4);

				ctx.write(byteBuf);

			} catch (TException ex) {
				ex.printStackTrace();
			}
		}
		
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			baseService.destory();
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
