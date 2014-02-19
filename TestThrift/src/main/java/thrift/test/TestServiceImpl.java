package thrift.test;

import org.apache.thrift.TException;

public class TestServiceImpl implements TestService.Iface {

	@Override
	public long ping(int length, TestMessage msg) throws TException {
		return 123;
	}

}
