package lindongjlu.test.thrift.tutorial;

import java.util.concurrent.Executors;

import lindongjlu.thrift.TBaseProcessor;
import lindongjlu.thrift.TBaseService;
import lindongjlu.thrift.TNettyNioSocketService;
import lindongjlu.thrift.TServiceImplFactory;
import lindongjlu.tutorial.CalculatorEx;
import io.netty.channel.nio.NioEventLoopGroup;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public class CalculatorServerEx {

	public static final int SERVER_PORT = 8090;

	public void startServer() {
		try {
			System.out.println("Test CalculatorServerEx start ....");

			NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
			TProtocolFactory protocoFactory = new TBinaryProtocol.Factory();
			
			
			
			TBaseProcessor<CalculatorEx.Iface> tprocessor = new CalculatorEx.Processor<CalculatorEx.Iface>();
			
			TNettyNioSocketService<CalculatorEx.Iface> service = 
					new TNettyNioSocketService<CalculatorEx.Iface>(tprocessor,
							new TServiceImplFactory<CalculatorEx.Iface>(){
								private final CalculatorServiceImplEx serviceImpl = new CalculatorServiceImplEx();
								@Override
								public TBaseService<CalculatorEx.Iface> getServiceImpl() {
									return serviceImpl;
								}
								
							}, 
							Executors.newFixedThreadPool(3),
							protocoFactory, protocoFactory, 
							eventLoopGroup, eventLoopGroup, 
							"localhost", SERVER_PORT);
			
			service.startAsync();
			
			service.awaitRunning();

		} catch (Exception e) {
			System.out.println("Server start error!!!");
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CalculatorServerEx server = new CalculatorServerEx();
		server.startServer();
	}
}
