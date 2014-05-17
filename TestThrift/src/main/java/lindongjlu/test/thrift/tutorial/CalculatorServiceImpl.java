package lindongjlu.test.thrift.tutorial;

import org.apache.thrift.TException;

import shared.SharedStruct;
import tutorial.Calculator;
import tutorial.InvalidOperation;
import tutorial.Work;

public class CalculatorServiceImpl implements Calculator.Iface {

	@Override
	public SharedStruct getStruct(int key) throws TException {
		return new SharedStruct(key, "ABCDE");
	}

	@Override
	public void ping() throws TException {
	}

	@Override
	public int add(int num1, int num2) throws TException {
		return num1 + num2;
	}

	@Override
	public int calculate(int logid, Work w) throws InvalidOperation, TException {
		
		throw new InvalidOperation(123, "what");
	}

	@Override
	public void zip() throws TException {
	}

}
