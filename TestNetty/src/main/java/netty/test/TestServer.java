package netty.test;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;

public class TestServer {

	private final int port = 1237;

	public TestServer() {
	}

	public void run() throws Exception {
		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		NioServerSocketChannel nioServerSocketChannel = new NioServerSocketChannel();
		nioServerSocketChannel.config().setBacklog(100);
		nioServerSocketChannel.pipeline().addLast(
				new Acceptor(workerGroup,
						new ChannelInitializer<SocketChannel>() {

							@Override
							protected void initChannel(SocketChannel ch)
									throws Exception {
								ch.pipeline().addLast(new EchoServerHandler());
							}

						}));

		bossGroup.register(nioServerSocketChannel).get();
		System.out.println("server channel register!");

		nioServerSocketChannel.bind(new InetSocketAddress("127.0.0.1", port))
				.get();
		System.out.println("server channel bind!");

		nioServerSocketChannel.closeFuture().sync();

		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	public static void main(String[] args) throws Exception {
		new TestServer().run();
	}

}

class Acceptor extends ChannelInboundHandlerAdapter {

	private final EventLoopGroup childGroup;
	private final ChannelHandler childHandler;
	private final Map<ChannelOption<?>, Object> childOptions;
	private final Map<AttributeKey<?>, Object> childAttrs;

	Acceptor(EventLoopGroup childGroup, ChannelHandler childHandler,
			Map<ChannelOption<?>, Object> childOptions,
			Map<AttributeKey<?>, Object> childAttrs) {
		this.childGroup = childGroup;
		this.childHandler = childHandler;
		this.childOptions = childOptions;
		this.childAttrs = childAttrs;
	}

	Acceptor(EventLoopGroup childGroup, ChannelHandler childHandler) {
		this.childGroup = childGroup;
		this.childHandler = childHandler;
		this.childOptions = Collections.emptyMap();
		this.childAttrs = Collections.emptyMap();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		
		final Channel child = (Channel) msg;
		
		System.out.println("accept: " + child.remoteAddress());
		
		child.pipeline().addLast(childHandler);

		for (Entry<ChannelOption<?>, Object> e : childOptions.entrySet()) {
			try {
				if (!child.config().setOption(
						(ChannelOption<Object>) e.getKey(), e.getValue())) {
					System.err.println("Unknown channel option: " + e);
				}
			} catch (Throwable t) {
				System.err.println("Failed to set a channel option: " + child);
				t.printStackTrace();
			}
		}

		for (Entry<AttributeKey<?>, Object> e : childAttrs.entrySet()) {
			child.attr((AttributeKey<Object>) e.getKey()).set(e.getValue());
		}

		try {
			childGroup.register(child).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					if (!future.isSuccess()) {
						forceClose(child, future.cause());
					}
				}
			});
		} catch (Throwable t) {
			forceClose(child, t);
		}
	}

	private static void forceClose(Channel child, Throwable t) {
		child.unsafe().closeForcibly();
		System.err.println("Failed to register an accepted channel: " + child);
		t.printStackTrace();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.fireExceptionCaught(cause);
	}
}

@Sharable
class EchoServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		byte[] bytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(bytes);
		System.out.println("recive: " + new String(bytes));
		// ctx.write(Unpooled.wrappedBuffer(bytes));
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
