package io.github.yellow013.cc.actor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import io.github.yellow013.cc.msg.Call;
import io.github.yellow013.cc.msg.CallResult;
import io.github.yellow013.cc.util.JsonParser;

public class Employees implements CallHandler, BiConsumer<byte[], byte[]> {

	// 提供自增编号
	private static final AtomicInteger i = new AtomicInteger();

	private final String name;

	public Employees() {
		this.name = "Employees[" + i.incrementAndGet() + "]";
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public CallResult onCall(Call call) {

		return null;
	}

	public static void main(String[] args) {

		for (int i = 0; i < 100; i++)
			System.out.println(new Employees());

	}

	@Override
	public void accept(byte[] topic, byte[] msg) {
		String str = new String(msg);
		Call call = JsonParser.toObject(str, Call.class);
		
		
	}

}
