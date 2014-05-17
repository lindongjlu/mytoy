package lindongjlu.test.thrift.tutorial;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransport;

import tutorial.Calculator;

public class CalculatorServer {

	public static final int SERVER_PORT = 8090;

	@SuppressWarnings("serial")
	public void startServer() {
		try {
			System.out.println("Test TSimpleServer start ....");

			TProcessor tprocessor = new Calculator.Processor<Calculator.Iface>(new CalculatorServiceImpl());
			// HelloWorldService.Processor<HelloWorldService.Iface> tprocessor =
			// new HelloWorldService.Processor<HelloWorldService.Iface>(
			// new HelloWorldImpl());

			// 简单的单线程服务模型，一般用于测试
			TServerSocket serverTransport = new TServerSocket(SERVER_PORT);
			TServer.Args tArgs = new TServer.Args(serverTransport);
			tArgs.processor(tprocessor);
			// tArgs.protocolFactory(new TBinaryProtocol.Factory());
			tArgs.protocolFactory(new TProtocolFactory() {
				
				@Override
				public TProtocol getProtocol(TTransport trans) {
					return new TBinaryProtocol.Factory().getProtocol(new TFramedTransport(trans));
				}
			});
			
			// tArgs.protocolFactory(new TCompactProtocol.Factory());
			// tArgs.protocolFactory(new TJSONProtocol.Factory());
			TServer server = new TSimpleServer(tArgs);
			server.serve();

		} catch (Exception e) {
			System.out.println("Server start error!!!");
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CalculatorServer server = new CalculatorServer();
		server.startServer();
	}

}
