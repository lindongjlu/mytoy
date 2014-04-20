package lindongjlu.thrift;

import org.apache.thrift.TBase;

import com.google.common.util.concurrent.ListenableFuture;

public class TServiceClientEx {

	private final TClientHandle handle;
	
	public TServiceClientEx(TClientHandle handle)
	{
		this.handle = handle;
	}
	
	public <T extends TBase<?, ?> > ListenableFuture<T> callBase(String methodName, TBase<?, ?> args, T result) {
		return handle.callBase(methodName, args, result);
	}
	
}
