package io.github.yellow013.cc.component;

import static io.github.yellow013.cc.transport.TransportConst.Addr;
import static io.github.yellow013.cc.transport.TransportConst.ResultPort;

import java.io.Closeable;
import java.io.IOException;

import io.github.yellow013.cc.collect.JctSingleConsumerQueue;
import io.github.yellow013.cc.msg.CallResult;
import io.github.yellow013.cc.transport.ZmqPublisher;
import io.github.yellow013.cc.transport.ZmqPublisher.ZmqPubConfigurator;
import io.github.yellow013.cc.util.JsonWrapper;

public class ResultCollector implements Closeable {

	private final ZmqPublisher<CallResult> publisher;

	private final JctSingleConsumerQueue<CallResult> inbox;

	public ResultCollector() {
		this.publisher = ZmqPublisher.create(ZmqPubConfigurator.tcp(Addr, ResultPort).setDefaultTopic("result"),
				result -> JsonWrapper.toJson(result).getBytes());
		this.inbox = JctSingleConsumerQueue.multiProducer("ResultCollector-Inbox").setCapacity(128)
				.buildWithConsumer(result -> {
					publisher.publish(result);
				});
	}

	public void onEvent(CallResult result) {
		inbox.enqueue(result);
	}

	@Override
	public void close() throws IOException {
		inbox.stop();
		publisher.close();
	}

}
