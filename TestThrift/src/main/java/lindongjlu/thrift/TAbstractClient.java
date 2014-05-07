package lindongjlu.thrift;

import org.apache.thrift.TBase;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public abstract class TAbstractClient<T extends TAbstractClient<?>> implements TBaseClient<T> {

	private final TBaseClient<?> baseClient;
	
	protected TAbstractClient(TBaseClient<?> baseClient) {
		this.baseClient = baseClient;
	}
	
	@Override
	public boolean isOpen() {
		return baseClient.isOpen();
	}

	@Override
	public ListenableFuture<T> open() {
		return Futures.transform(baseClient.open(), new Function<TBaseClient<?>, T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T apply(TBaseClient<?> input) {
				return (T) TAbstractClient.this;
			}
		});
	}

	@Override
	public ListenableFuture<T> close() {
		return Futures.transform(baseClient.close(), new Function<TBaseClient<?>, T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T apply(TBaseClient<?> input) {
				return (T) TAbstractClient.this;
			}
		});
	}

	@Override
	public <R extends TBase<?, ?>> ListenableFuture<R> callBase(
			String methodName, TBase<?, ?> args, R result) {
		return baseClient.callBase(methodName, args, result);
	}
	
}
