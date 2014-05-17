package lindongjlu.thrift;

import org.apache.thrift.TBase;

import com.google.common.base.Function;
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

	private final Function<TBaseClient, TAbstractClient> transformFunc = new Function<TBaseClient, TAbstractClient>() {

		@Override
		public TAbstractClient apply(TBaseClient input) {
			return TAbstractClient.this;
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
