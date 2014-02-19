package thrift.test;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

public class TestServiceImpl implements TestService.Iface {

	@Override
	public long ping(int length, TestMessage msg) throws TException {
		TTransport trans = new TIOStreamTransport(System.out);
		msg.write(new TJSONProtocol(trans));
		msg.write(new TSimpleJSONProtocol(trans));
		trans.close();
		return 123;
	}

}
