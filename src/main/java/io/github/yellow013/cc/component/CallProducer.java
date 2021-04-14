package io.github.yellow013.cc.component;

import static io.github.yellow013.cc.transport.TransportConst.Addr;
import static io.github.yellow013.cc.transport.TransportConst.CallPort;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;

import io.github.yellow013.cc.msg.Call;
import io.github.yellow013.cc.transport.ZmqPublisher;
import io.github.yellow013.cc.transport.ZmqPublisher.ZmqPubConfigurator;
import io.github.yellow013.cc.util.JsonWrapper;
import io.github.yellow013.cc.util.Threads;

public class CallProducer implements Runnable, Closeable {

	private final ZmqPublisher<Call> publisher;

	public CallProducer() {
		this.publisher = ZmqPublisher.create(ZmqPubConfigurator.tcp(Addr, CallPort).setDefaultTopic("call"),
				call -> JsonWrapper.toJson(call).getBytes());
	}

	@Override
	public void run() {
		final Random random = new Random();
		for (;;) {
			int envelope = Math.abs(random.nextInt()) % 10;
			publisher.publish(new Call(envelope, "question : " + envelope));
			Threads.sleep(100);
		}
	}

	public void start() {
		Threads.startNewThread(this);
	}

	@Override
	public void close() throws IOException {
		publisher.destroy();
	}
	
	public static void main(String[] args) {
		try (CallProducer producer = new CallProducer()) {
			producer.start();
			Thread.currentThread().join();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
