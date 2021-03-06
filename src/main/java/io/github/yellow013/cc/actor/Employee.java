package io.github.yellow013.cc.actor;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.github.yellow013.cc.component.ResultCollector;
import io.github.yellow013.cc.msg.Call;
import io.github.yellow013.cc.msg.CallResult;
import io.github.yellow013.cc.util.Threads;

public class Employee implements CallHandler, Runnable {

	// 提供自增编号
	private static final AtomicInteger i = new AtomicInteger();

	private final String name;

	private final CallHandler superior;

	private final SynchronousQueue<Call> inHandle = new SynchronousQueue<>();

	private final ResultCollector collector;

	public Employee(CallHandler superior, ResultCollector collector) {
		this.name = "Employees[" + i.incrementAndGet() + "]";
		this.superior = superior;
		this.collector = collector;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean onCall(Call call) {
		if (isCanHandle(call.getEnvelope())) {
			return inHandle.offer(call);
		} else {
			superior.onCall(call);
			return true;
		}
	}

	private boolean isCanHandle(int envelope) {
		return envelope < 7 ? true : false;
	}

	@Override
	public void run() {
		try {
			for (;;) {
				// 代表需要的处理时间
				Threads.sleep(1200);
				Call call = inHandle.take();
				collector.onEvent(new CallResult(call.getSeq(), 1, name + " processed [" + call.getMsg() + "]"));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		AtomicReference<String> inHandle = new AtomicReference<>();

		System.out.println(inHandle.compareAndSet(null, null));

		SynchronousQueue<String> exchange = new SynchronousQueue<>();

		Threads.startNewThread(() -> {
			for (int i = 0; i < 10000; i++) {
				System.out.println(i + " : " + exchange.offer(Integer.toString(i)));
			}
		});

		Threads.startNewThread(() -> {
			for (;;) {
				try {
					String take = exchange.take();
					System.out.println(take);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		Threads.join();

	}

}
