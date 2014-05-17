package lindongjlu.thrift;

import java.util.Collections;
import java.util.Map;

import org.apache.thrift.TBase;

public abstract class TBaseProcessor<I> {

	private final Map<String, TProcessFunction<I, ? extends TBase>> processMap;

	protected TBaseProcessor(Map<String, TProcessFunction<I, ? extends TBase>> processFunctionMap) {
		this.processMap = processFunctionMap;
	}

	public Map<String, TProcessFunction<I, ? extends TBase>> getProcessMapView() {
		return Collections.unmodifiableMap(processMap);
	}
	
	public TProcessFunction<I, ? extends TBase> getProccessFunction(String methodName) {
		return processMap.get(methodName);
	}
	
}
