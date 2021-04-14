package io.github.yellow013.cc.actor;

import java.util.concurrent.atomic.AtomicInteger;

import io.github.yellow013.cc.collect.JctSingleConsumerQueue;
import io.github.yellow013.cc.component.ResultCollector;
import io.github.yellow013.cc.msg.Call;
import io.github.yellow013.cc.msg.CallResult;

public class TechnicalLeader implements CallHandler {

	// 提供自增编号
	private static final AtomicInteger i = new AtomicInteger();

	private final JctSingleConsumerQueue<Call> inbox;

	private final String name;

	public TechnicalLeader(final CallHandler superior, final ResultCollector collector) {
		this.name = "TechnicalLeader[" + i.incrementAndGet() + "]";
		this.inbox = JctSingleConsumerQueue.multiProducer(name + "-Inbox").setCapacity(64).buildWithConsumer(call -> {
			if (isCanHandle(call.getEnvelope()))
				collector.onEvent(new CallResult(call.getSeq(), 1, name + " processed [" + call.getMsg() + "]"));
			else
				superior.onCall(call);
		});
	}

	@Override
	public boolean onCall(Call call) {
		return inbox.enqueue(call);
	}

	private boolean isCanHandle(int envelope) {
		return envelope < 9 ? true : false;
	}

}
