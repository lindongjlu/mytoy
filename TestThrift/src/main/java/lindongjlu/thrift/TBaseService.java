package lindongjlu.thrift;

public interface TBaseService<I> {

	void initialize();
	void destory();
	I getService();
	
}
