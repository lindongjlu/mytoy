package lindongjlu.test.thrift.tutorial;

import io.netty.channel.nio.NioEventLoopGroup;
import lindongjlu.thrift.TNettyNioSocketClient;
import lindongjlu.tutorial.CalculatorEx;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public class CalculatorTestEx {

	public static final String SERVER_IP = "localhost";
	public static final int SERVER_PORT = 8090;
	public static final int TIMEOUT = 30000;

	/**
	 *
	 * @param userName
	 * @throws Throwable 
	 */
	public void startClient() throws Throwable {
		
		// netty init start
		
		NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		
		TProtocolFactory protocoFactory = new TBinaryProtocol.Factory();
		TNettyNioSocketClient baseClient = new TNettyNioSocketClient(protocoFactory, protocoFactory, eventLoopGroup, SERVER_IP, SERVER_PORT);
		
		// netty init end
		
		CalculatorEx.Client client = new CalculatorEx.Client(baseClient);
		
		client.open().get();
		
		int result = client.add(123, 456).get();

		System.out.println("Calculator client result =: " + result);
		
		client.close().get();
		eventLoopGroup.shutdownGracefully().get();
	}

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		CalculatorTestEx test = new CalculatorTestEx();
		test.startClient();
	}

}