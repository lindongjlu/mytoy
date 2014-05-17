package thrift.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TBase;
import lindongjlu.thrift.TAbstractClient;
import lindongjlu.thrift.TBaseClient;
import thrift.test.TestService.ping_args;
import thrift.test.TestService.ping_result;
import com.google.common.base.Function;
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
	
	public static class Processor<I extends Iface> extends lindongjlu.thrift.TBaseProcessor<I> {
		
	    public Processor() {
	      super(getProcessMap(new HashMap<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
	    }

	    protected Processor(Map<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
	      super(getProcessMap(processMap));
	    }

	    private static <I extends Iface> Map<String, lindongjlu.thrift.TProcessFunction<I, ? extends org.apache.thrift.TBase>> getProcessMap(Map<String, lindongjlu.thrift.TProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
	      processMap.put("ping", new ping());
	      return processMap;
	    }

	    public static class ping<I extends Iface> extends lindongjlu.thrift.TProcessFunction<I, ping_args> {
	      
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
	      public ListenableFuture<TBase> process(I iface, ping_args args) throws org.apache.thrift.TException {
	    	  return Futures.transform(iface.ping(args.length, args.msg), new Function<Long, TBase>() {

				@Override
				public TBase apply(Long input) {
					ping_result result = new ping_result();
			        result.success = input;
			        result.setSuccessIsSet(true);
					return result;
				}
				
	    	  });
	      }
	    }

	  }

}
