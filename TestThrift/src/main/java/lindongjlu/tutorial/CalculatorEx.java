package lindongjlu.tutorial;

import lindongjlu.thrift.TBaseClient;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import tutorial.Calculator.add_args;
import tutorial.Calculator.add_result;
import tutorial.Calculator.calculate_args;
import tutorial.Calculator.calculate_result;
import tutorial.Calculator.ping_args;
import tutorial.Calculator.ping_result;
import tutorial.Calculator.zip_args;
import tutorial.Work;

public class CalculatorEx {


	public interface Iface extends lindongjlu.tutorial.SharedServiceEx.Iface {

		ListenableFuture<Void> ping();

		ListenableFuture<Integer> add(int num1, int num2);

		// throws InvalidOperation
		ListenableFuture<Integer> calculate(int logid, Work w);

		// one way
		ListenableFuture<Void> zip();

	}
	
	@SuppressWarnings("unchecked")
	public static class Client extends lindongjlu.tutorial.SharedServiceEx.Client implements Iface {

		public Client(TBaseClient baseClient) {
			super(baseClient);
		}
		
		@Override
		public ListenableFuture<? extends Client> open() {
			return (ListenableFuture<? extends Client>) super.open();
		}
		
		@Override
		public ListenableFuture<? extends Client> close() {
			return (ListenableFuture<? extends Client>) super.open();
		}

		@Override
		public ListenableFuture<Void> ping() {
			ping_args args = new ping_args();
			return Futures.transform(super.<ping_result> callBase(
					"ping", args, new ping_result()),
					new AsyncFunction<ping_result, Void>() {

						@Override
						public ListenableFuture<Void> apply(ping_result result)
								throws Exception {
							return Futures.immediateFuture(null);
						}

					});
		}

		@Override
		public ListenableFuture<Integer> add(int num1, int num2) {
			add_args args = new add_args();
			args.setNum1(num1);
			args.setNum2(num2);
			return Futures.transform(super.<add_result> callBase(
					"add", args, new add_result()),
					new AsyncFunction<add_result, Integer>() {

						@Override
						public ListenableFuture<Integer> apply(
								add_result result) throws Exception {
							if (result.isSetSuccess()) {
								return Futures.immediateFuture(result.success);
							}
							return Futures
									.immediateFailedFuture(new org.apache.thrift.TApplicationException(
											org.apache.thrift.TApplicationException.MISSING_RESULT,
											"add failed: unknown result"));
						}

					});
		}

		@Override
		public ListenableFuture<Integer> calculate(int logid, Work w) {
			calculate_args args = new calculate_args();
			args.setLogid(logid);
			args.setW(w);
			return Futures.transform(super.<calculate_result> callBase(
					"calculate", args, new calculate_result()),
					new AsyncFunction<calculate_result, Integer>() {

						@Override
						public ListenableFuture<Integer> apply(
								calculate_result result) throws Exception {
							if (result.isSetSuccess()) {
								return Futures.immediateFuture(result.success);
							}
							if (result.ouch != null) {
								return Futures.immediateFailedFuture(result.ouch);
							}
							return Futures
									.immediateFailedFuture(new org.apache.thrift.TApplicationException(
											org.apache.thrift.TApplicationException.MISSING_RESULT,
											"add failed: unknown result"));
						}

					});
		}

		@Override
		public ListenableFuture<Void> zip() {
			zip_args args = new zip_args();
			return super.callOneWay("zip", args);
		}
		
	}
	
}
