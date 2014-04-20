package lindongjlu.thrift;

import org.apache.thrift.TBase;

import com.google.common.util.concurrent.ListenableFuture;

public interface TClientHandle {

	<T extends TBase<?, ?> > ListenableFuture<T> callBase(String methodName, TBase<?, ?> args, T result);
	
}
