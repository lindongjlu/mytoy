package netty.test;

import java.net.InetSocketAddress;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TestClient {

	private final String host;
	private final int port;

	public TestClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() throws Exception {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

		NioSocketChannel nioSocketChannel = new NioSocketChannel();
		nioSocketChannel.config().setTcpNoDelay(true);
		// nioSocketChannel.pipeline().addLast(new EmptyHandler());

		eventLoopGroup.register(nioSocketChannel).get();
		System.out.println("channel register!");

		nioSocketChannel.connect(new InetSocketAddress(host, port)).get();
		System.out.println("channel connect!");

		System.out.println("channel isActive:" + nioSocketChannel.isActive());

		for(int i=0; i<10; ++i)
		{
			nioSocketChannel.writeAndFlush(Unpooled.wrappedBuffer(("你好" + i).getBytes())).sync();
		}
		
		nioSocketChannel.close().sync();
		System.out.println("channel close!");

		eventLoopGroup.shutdownGracefully();
	}

	public static void main(String[] args) throws Exception {
		new TestClient("127.0.0.1", 1237).run();
	}
}

class EmptyHandler extends ChannelInboundHandlerAdapter {

	/**
	 * Creates a client-side handler.
	 */
	public EmptyHandler() {
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// ctx.write(msg);
		System.out.println(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
