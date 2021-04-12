package io.github.yellow013.cc.collect;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author yellow013
 *
 * @param <T> Single Consumer Queue base implements
 */
public abstract class SingleConsumerQueue<E> {

	/**
	 * Processor Function
	 */
	protected final Consumer<E> consumer;

	/**
	 * Running flag
	 */
	protected final AtomicBoolean isRunning = new AtomicBoolean(false);

	/**
	 * Close flag
	 */
	protected final AtomicBoolean isClosed = new AtomicBoolean(false);

	protected String queueName = "SCQ-" + Long.toString(Math.abs(ThreadLocalRandom.current().nextLong()));

	protected SingleConsumerQueue(Consumer<E> consumer) {
		if (consumer == null)
			throw new NullPointerException("consumer can not be null");
		this.consumer = consumer;
	}

	protected abstract void startProcessThread();

	public void start() {
		startProcessThread();
	}

	public void stop() {
		this.isRunning.set(false);
		this.isClosed.set(true);
	}

	public String getQueueName() {
		return queueName;
	}

}
