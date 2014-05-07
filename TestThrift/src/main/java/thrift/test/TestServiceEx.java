package thrift.test;

import lindongjlu.thrift.TAbstractClient;
import lindongjlu.thrift.TBaseClient;
import thrift.test.TestService.ping_args;
import thrift.test.TestService.ping_result;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class TestServiceEx {

	public interface Iface {
		public com.google.common.util.concurrent.ListenableFuture<Long> ping(
				int length, TestMessage msg);
	}
	
	public static class Client extends TAbstractClient<Client> implements Iface {

		public Client(TBaseClient<?> baseClient) {
			super(baseClient);
		}

		@Override
		public ListenableFuture<Long> ping(int length, TestMessage msg) {
			
			ping_args args = new ping_args();
		    args.setLength(length);
		    args.setMsg(msg);

			return Futures.transform(super.<ping_result> callBase("ping", args, new ping_result()), 
					new AsyncFunction<ping_result, Long>() {

				@Override
				public ListenableFuture<Long> apply(ping_result result) {
					if (result.isSetSuccess()) {
						return Futures.immediateFuture(result.success);
					}
					return Futures.immediateFailedFuture(new org.apache.thrift.TApplicationException(
							org.apache.thrift.TApplicationException.MISSING_RESULT,
							"ping failed: unknown result"));
				}
				
			});
		}
		
	}

}