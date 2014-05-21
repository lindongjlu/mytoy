package lindongjlu.thrift;

public interface TServiceImplFactory<I> {

	TBaseService<I> getServiceImpl();
	
}
