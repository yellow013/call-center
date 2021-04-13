package io.github.yellow013.cc.component;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.BiConsumer;

import io.github.yellow013.cc.transport.ZmqSubscriber;
import io.github.yellow013.cc.util.Threads;

public class CallRouter implements BiConsumer<byte[], byte[]>, Closeable {

	private final ZmqSubscriber subscriber;

	public CallRouter() {
		this.subscriber = ZmqSubscriber.tcp("127.0.0.1", 5556).setTopics("call").build(this);
	}

	public void start() {
		Threads.startNewThread(subscriber);
	}

	@Override
	public void accept(byte[] topic, byte[] msg) {
		
	}

	@Override
	public void close() throws IOException {
		subscriber.close();
	}

}
