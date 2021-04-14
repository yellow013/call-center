package io.github.yellow013.cc.actor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import io.github.yellow013.cc.msg.Call;
import io.github.yellow013.cc.msg.CallResult;
import io.github.yellow013.cc.util.JsonParser;

public class Employees implements CallHandler, BiConsumer<byte[], byte[]> {

	// 提供自增编号
	private static final AtomicInteger i = new AtomicInteger();

	private final String name;

	private final CallHandler superior;
	
	private AtomicReference<Call> inHandle = new AtomicReference<>();

	public Employees(CallHandler superior) {
		this.name = "Employees[" + i.incrementAndGet() + "]";
		this.superior = superior;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void onCall(Call call) {
		if (isCanHandle(call.getEnvelope())) {
			if(inHandle.compareAndSet(null, call));
		} else {
			superior.onCall(call);
		}
	}

	private boolean isCanHandle(int envelope) {
		return envelope < 7 ? true : false;
	}

	@Override
	public void accept(byte[] topic, byte[] msg) {
		String str = new String(msg);
		Call call = JsonParser.toObject(str, Call.class);

	}
	
	
	public static void main(String[] args) {
		 AtomicReference<String> inHandle = new AtomicReference<>();
		 
		 System.out.println(inHandle.compareAndSet(null, null));
		 
	}

}
