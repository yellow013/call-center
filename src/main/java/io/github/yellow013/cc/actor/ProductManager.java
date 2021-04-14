package io.github.yellow013.cc.actor;

import java.util.concurrent.atomic.AtomicInteger;

import io.github.yellow013.cc.collect.JctSingleConsumerQueue;
import io.github.yellow013.cc.component.ResultCollector;
import io.github.yellow013.cc.msg.Call;
import io.github.yellow013.cc.msg.CallResult;

public class ProductManager implements CallHandler {

	// 提供自增编号
	private static final AtomicInteger i = new AtomicInteger();

	private final JctSingleConsumerQueue<Call> inbox;

	private final String name;

	public ProductManager(final ResultCollector collector) {
		this.name = "ProductManager[" + i.incrementAndGet() + "]";
		this.inbox = JctSingleConsumerQueue.multiProducer(name + "-Inbox").setCapacity(64).buildWithConsumer(call -> {
			collector.onEvent(new CallResult(call.getSeq(), 1, name + " processed [" + call.getMsg() + "]"));
		});
	}

	@Override
	public boolean onCall(Call call) {
		return inbox.enqueue(call);
	}

}
