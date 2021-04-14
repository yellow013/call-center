package io.github.yellow013.cc.component;

import static io.github.yellow013.cc.transport.TransportConst.Addr;
import static io.github.yellow013.cc.transport.TransportConst.CallPort;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.BiConsumer;

import io.github.yellow013.cc.actor.Employee;
import io.github.yellow013.cc.actor.ProductManager;
import io.github.yellow013.cc.actor.TechnicalLeader;
import io.github.yellow013.cc.msg.Call;
import io.github.yellow013.cc.transport.ZmqSubscriber;
import io.github.yellow013.cc.util.JsonParser;
import io.github.yellow013.cc.util.Threads;

public class CallDealer implements BiConsumer<byte[], byte[]>, Closeable {

	private final ZmqSubscriber subscriber;

	private final ProductManager pm;

	private final TechnicalLeader tl;

	private final Employee[] employees = new Employee[10];

	public CallDealer() {
		this.subscriber = ZmqSubscriber.tcp(Addr, CallPort).setTopics("call").build(this);
		ResultCollector collector = new ResultCollector();
		pm = new ProductManager(collector);
		tl = new TechnicalLeader(pm, collector);
		for (int i = 0; i < employees.length; i++) {
			employees[i] = new Employee(tl, collector);
		}
	}

	public void start() {
		Threads.startNewThread(subscriber);
	}

	@Override
	public void accept(byte[] t, byte[] m) {
		String topic = new String(t);
		String msg = new String(m);
		if (topic.equals("call")) {
			Call call = JsonParser.toObject(msg, Call.class);
			for (int i = 0; i < employees.length; i++) {
				boolean b = employees[i].onCall(call);
				if (b) {
					return;
				}
			}
			tl.onCall(call);
		}

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
