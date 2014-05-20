package lindongjlu.thrift;

import org.apache.thrift.TBase;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public abstract class TAbstractClient implements TBaseClient {

	private final TBaseClient baseClient;

	protected TAbstractClient(TBaseClient baseClient) {
		this.baseClient = baseClient;
	}

	@Override
	public boolean isOpen() {
		return baseClient.isOpen();
	}

	private final AsyncFunction<TBaseClient, TAbstractClient> transformFunc = new AsyncFunction<TBaseClient, TAbstractClient>() {

		@Override
		public ListenableFuture<TAbstractClient> apply(TBaseClient input) {
			return Futures.immediateFuture(TAbstractClient.this);
		}
	};

	@Override
	public ListenableFuture<? extends TAbstractClient> open() {
		return Futures.transform(baseClient.open(), transformFunc);
	}

	@Override
	public ListenableFuture<? extends TAbstractClient> close() {
		return Futures.transform(baseClient.close(), transformFunc);
	}

	@Override
	public <R extends TBase<?, ?>> ListenableFuture<R> callBase(
			String methodName, TBase<?, ?> args, R result) {
		return baseClient.callBase(methodName, args, result);
	}
	
	@Override
	public ListenableFuture<Void> callOneWay(String methodName, TBase<?, ?> args) {
		return baseClient.callOneWay(methodName, args);
	}

}
