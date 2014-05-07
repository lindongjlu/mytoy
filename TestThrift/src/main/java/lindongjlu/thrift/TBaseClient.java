package lindongjlu.thrift;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.google.common.util.concurrent.ListenableFuture;

public interface TBaseClient<T extends TBaseClient<?>> {

	boolean isOpen();

	/**
	 * @throws TTransportException
	 */
	ListenableFuture<T> open();

	ListenableFuture<T> close();

	/**
	 * @throws TException
	 */
	<R extends TBase<?, ?>> ListenableFuture<R> callBase(
			String methodName, TBase<?, ?> args, R result);
}
