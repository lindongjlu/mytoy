package lindongjlu.thrift;

import java.net.InetSocketAddress;

public interface TBaseService<I> {

	void initialize(InetSocketAddress remoteAddress);
	void destory();
	I asService();
	
}
