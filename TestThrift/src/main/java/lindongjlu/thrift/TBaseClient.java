package lindongjlu.thrift;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.google.common.util.concurrent.ListenableFuture;

public interface TBaseClient {

	boolean isOpen();

	/**
	 * @throws TTransportException
	 */
	ListenableFuture<? extends TBaseClient> open();

	ListenableFuture<? extends TBaseClient> close();

	/**
	 * @throws TException
	 */
	<T extends TBase<?, ?>> ListenableFuture<T> callBase(
			String methodName, TBase<?, ?> args, T result);
	
	ListenableFuture<Void> callOneWay(String methodName, TBase<?, ?> args);
}
