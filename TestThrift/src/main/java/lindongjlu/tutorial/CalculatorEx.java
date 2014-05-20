package lindongjlu.tutorial;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TBase;

import lindongjlu.thrift.TBaseClient;
import lindongjlu.tutorial.SharedServiceEx.Iface;
import lindongjlu.tutorial.SharedServiceEx.Processor.getStruct;

import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import shared.SharedStruct;
import shared.SharedService.getStruct_args;
import shared.SharedService.getStruct_result;
import tutorial.Calculator.add_args;
import tutorial.Calculator.add_result;
import tutorial.Calculator.calculate_args;
import tutorial.Calculator.calculate_result;
import tutorial.Calculator.ping_args;
import tutorial.Calculator.ping_result;
import tutorial.Calculator.zip_args;
import tutorial.InvalidOperation;
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
			return (ListenableFuture<? extends Client>) super.close();
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
	
	public static class Processor<I extends Iface> extends lindongjlu.tutorial.SharedServiceEx.Processor<I> {
	
		public Processor() {
			super(getProcessMap(new HashMap<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
		}
		
		protected Processor(
				Map<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
			super(getProcessMap(processMap));
		}
		
		private static <I extends Iface> Map<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>> getProcessMap(
				Map<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
			processMap.put("ping", new ping<I>());
			processMap.put("add", new add<I>());
			processMap.put("calculate", new calculate<I>());
			processMap.put("zip", new zip<I>());
			return processMap;
		}
		
		public static class ping<I extends Iface> extends
				lindongjlu.thrift.TProcessFunction<I, ping_args> {
		
			public ping() {
				super("ping");
			}
		
			@Override
			public ping_args getEmptyArgsInstance() {
				return new ping_args();
			}
		
			@Override
			public boolean isOneway() {
				return false;
			}
		
			@Override
			public ListenableFuture<org.apache.thrift.TBase> process(I iface, ping_args args)
					throws org.apache.thrift.TException {
				return Futures.transform(iface.ping(), 
						new Function<Void, org.apache.thrift.TBase>() {
		
							@Override
							public org.apache.thrift.TBase apply(Void input) {
								ping_result result = new ping_result();
								return result;
							}
		
						});
			}
		}
				
		public static class add<I extends Iface> extends
				lindongjlu.thrift.TProcessFunction<I, add_args> {
		
			public add() {
				super("add");
			}
		
			@Override
			public add_args getEmptyArgsInstance() {
				return new add_args();
			}
		
			@Override
			public boolean isOneway() {
				return false;
			}
		
			@Override
			public ListenableFuture<org.apache.thrift.TBase> process(I iface, add_args args)
					throws org.apache.thrift.TException {
				return Futures.transform(iface.add(args.num1, args.num2), 
						new Function<Integer, org.apache.thrift.TBase>() {
		
							@Override
							public org.apache.thrift.TBase apply(Integer input) {
								add_result result = new add_result();
								result.success = input;
								result.setSuccessIsSet(true);
								return result;
							}
		
						});
			}
		}
				
		public static class calculate<I extends Iface> extends
				lindongjlu.thrift.TProcessFunction<I, calculate_args> {
		
			public calculate() {
				super("calculate");
			}
		
			@Override
			public calculate_args getEmptyArgsInstance() {
				return new calculate_args();
			}
		
			@Override
			public boolean isOneway() {
				return false;
			}
		
			@Override
			public ListenableFuture<org.apache.thrift.TBase> process(I iface, calculate_args args)
					throws org.apache.thrift.TException {
				return Futures.withFallback(Futures.transform(iface.calculate(args.logid, args.w), 
						new Function<Integer, org.apache.thrift.TBase>() {
		
							@Override
							public org.apache.thrift.TBase apply(Integer input) {
								calculate_result result = new calculate_result();
								result.success = input;
								result.setSuccessIsSet(true);
								return result;
							}
		
						}), new FutureFallback<org.apache.thrift.TBase>() {

							@Override
							public ListenableFuture<org.apache.thrift.TBase> create(Throwable t)
									throws Exception {
								if (t instanceof InvalidOperation) {
									calculate_result result = new calculate_result();
									result.ouch = (InvalidOperation) t;
									return Futures.<org.apache.thrift.TBase>immediateFuture(result);
								}
								return Futures.immediateFailedFuture(t);
							}
						});
			}
		}
				
		public static class zip<I extends Iface> extends
				lindongjlu.thrift.TProcessFunction<I, zip_args> {
		
			public zip() {
				super("zip");
			}
		
			@Override
			public zip_args getEmptyArgsInstance() {
				return new zip_args();
			}
		
			@Override
			public boolean isOneway() {
				return true;
			}
		
			@Override
			public ListenableFuture<org.apache.thrift.TBase> process(I iface, zip_args args)
					throws org.apache.thrift.TException {
				return Futures.transform(iface.zip(), 
						new Function<Void, org.apache.thrift.TBase>() {
		
							@Override
							public org.apache.thrift.TBase apply(Void input) {
								return null;
							}
		
						});
			}
		}
	
	}
	
}
