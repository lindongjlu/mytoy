package lindongjlu.test.thrift.tutorial;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import shared.SharedStruct;
import tutorial.InvalidOperation;
import tutorial.Work;
import lindongjlu.tutorial.CalculatorEx;

public class CalculatorServiceImplEx implements CalculatorEx.Iface {

	@Override
	public ListenableFuture<SharedStruct> getStruct(int key) {
		return Futures.immediateFuture(new SharedStruct(key, "ABCDE"));
	}

	@Override
	public ListenableFuture<Void> ping() {
		return Futures.immediateFuture(null);
	}

	@Override
	public ListenableFuture<Integer> add(int num1, int num2) {
		return Futures.immediateFuture(num1 + num2);
	}

	@Override
	public ListenableFuture<Integer> calculate(int logid, Work w) {
		return Futures.immediateFailedFuture(new InvalidOperation(123, "what"));
	}

	@Override
	public ListenableFuture<Void> zip() {
		return Futures.immediateFuture(null);
	}

}
