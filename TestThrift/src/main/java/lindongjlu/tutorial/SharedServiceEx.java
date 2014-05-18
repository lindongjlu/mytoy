package lindongjlu.tutorial;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import shared.SharedService.getStruct_result;
import shared.SharedStruct;
import shared.SharedService.getStruct_args;

public class SharedServiceEx {

	public interface Iface {
		ListenableFuture<SharedStruct> getStruct(int key);
	}

	@SuppressWarnings("unchecked")
	public static class Client extends lindongjlu.thrift.TAbstractClient implements Iface {

		public Client(lindongjlu.thrift.TBaseClient baseClient) {
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
		public ListenableFuture<SharedStruct> getStruct(int key) {
			getStruct_args args = new getStruct_args();
			args.setKey(key);
			return Futures.transform(super.<getStruct_result> callBase(
					"getStruct", args, new getStruct_result()),
					new AsyncFunction<getStruct_result, SharedStruct>() {

						@Override
						public ListenableFuture<SharedStruct> apply(
								getStruct_result result) throws Exception {
							if (result.isSetSuccess()) {
								return Futures.immediateFuture(result.success);
							}
							return Futures
									.immediateFailedFuture(new org.apache.thrift.TApplicationException(
											org.apache.thrift.TApplicationException.MISSING_RESULT,
											"getStruct failed: unknown result"));
						}

					});
		}

	}

	public static class Processor<I extends Iface> extends
			lindongjlu.thrift.TBaseProcessor<I> {

		public Processor() {
			super(getProcessMap(new HashMap<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
		}

		protected Processor(
				Map<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
			super(getProcessMap(processMap));
		}

		private static <I extends Iface> Map<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>> getProcessMap(
				Map<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
			processMap.put("getStruct", new getStruct());
			return processMap;
		}

		public static class getStruct<I extends Iface> extends
				lindongjlu.thrift.TProcessFunction<I, getStruct_args> {

			public getStruct() {
				super("getStruct");
			}

			@Override
			public getStruct_args getEmptyArgsInstance() {
				return new getStruct_args();
			}

			@Override
			public boolean isOneway() {
				return false;
			}

			@Override
			public ListenableFuture<org.apache.thrift.TBase> process(I iface, getStruct_args args)
					throws org.apache.thrift.TException {
				return Futures.transform(iface.getStruct(args.key), 
						new Function<SharedStruct, org.apache.thrift.TBase>() {

							@Override
							public org.apache.thrift.TBase apply(SharedStruct input) {
								getStruct_result result = new getStruct_result();
						        result.success = input;
								return result;
							}

						});
			}
		}

	}

}
