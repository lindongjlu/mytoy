package thrift.test;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;

import com.google.common.util.concurrent.ListenableFuture;

import lindongjlu.thrift.TNettyHandle;
import lindongjlu.thrift.TNettyNioSocketClient;

public class TestClientEx {

	public static final String SERVER_IP = "localhost";
	public static final int SERVER_PORT = 8090;
	public static final int TIMEOUT = 30000;
	
	public void startClient() throws Throwable {
		
		// netty init start
		
//		TNettyHandle nettyHandle = new TNettyHandle(new TBinaryProtocol.Factory());
//		
//		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
//
//		NioSocketChannel nioSocketChannel = new NioSocketChannel();
//		nioSocketChannel.config().setTcpNoDelay(true);
//		nioSocketChannel.pipeline().addLast(nettyHandle);
//
//		eventLoopGroup.register(nioSocketChannel).get();
//		System.out.println("channel register!");
//
//		nioSocketChannel.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT)).get();
//		System.out.println("channel connect!");
		
		NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		
		TProtocolFactory protocoFactory = new TBinaryProtocol.Factory();
		TNettyNioSocketClient baseClient = new TNettyNioSocketClient(protocoFactory, protocoFactory, eventLoopGroup, SERVER_IP, SERVER_PORT);
		
		// netty init end
		
		TestServiceEx.Client client = new TestServiceEx.Client(baseClient);
		
		client.open().get();
		
		TestMessage testMsg = new TestMessage("topic", ByteBuffer.wrap("123".getBytes()), 2, "哇咔咔", 123);
		
		ListenableFuture<Long> f = client.ping(279, testMsg);
		System.out.println("Thrify client result =: " + f.get());
		
		client.close().get();
		eventLoopGroup.shutdownGracefully().get();
	}
	
	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		TestClientEx client = new TestClientEx();
		client.startClient();
	}
}
