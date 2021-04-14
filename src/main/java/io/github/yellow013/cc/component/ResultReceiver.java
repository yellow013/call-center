package io.github.yellow013.cc.component;

import static io.github.yellow013.cc.transport.TransportConst.Addr;
import static io.github.yellow013.cc.transport.TransportConst.Port;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.BiConsumer;

import io.github.yellow013.cc.transport.ZmqSubscriber;
import io.github.yellow013.cc.util.Threads;

public class ResultReceiver implements BiConsumer<byte[], byte[]>, Closeable {

	private final ZmqSubscriber subscriber;

	public ResultReceiver() {
		this.subscriber = ZmqSubscriber.tcp(Addr, Port).setTopics("result").build(this);
	}

	public void start() {
		Threads.startNewThread(subscriber);
	}

	@Override
	public void accept(byte[] t, byte[] m) {
		String topic = new String(t);
		String msg = new String(m);
		System.out.println("recv -> " + topic + " : " + msg);
	}

	@Override
	public void close() throws IOException {
		subscriber.close();
	}

	public static void main(String[] args) {
		try (CallDealer dealer = new CallDealer()) {
			dealer.start();
			Thread.currentThread().join();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
