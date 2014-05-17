package lindongjlu.thrift;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import com.google.common.util.concurrent.ListenableFuture;

public abstract class TProcessFunction<I, T extends TBase> {

	private final String methodName;

	public TProcessFunction(String methodName) {
		this.methodName = methodName;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public abstract T getEmptyArgsInstance();
	
	public abstract boolean isOneway();

	public abstract ListenableFuture<TBase> process(I iface, T args) throws TException;
	
}
