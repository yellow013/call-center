package io.github.yellow013.cc.collect;

import java.util.Queue;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.SpscArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.yellow013.cc.util.Threads;

/**
 * 
 * Single Consumer Queue
 * 
 * @author yellow013
 *
 * @see <a>https://github.com/yellow013/mercury/blob/master/commons/commons-concurrent/src/main/java/io/mercury/common/concurrent/queue/jct/JctSingleConsumerQueue.java</a>
 * 
 * @param <T>
 * 
 */
public abstract class JctSingleConsumerQueue<E> extends SingleConsumerQueue<E> {

	// Logger
	private static final Logger log = LoggerFactory.getLogger(JctSingleConsumerQueue.class);

	// internal queue
	protected final Queue<E> queue;

	// consumer runnable
	protected final Runnable runnable;

	// waiting strategy
	private final WaitingStrategy strategy;

	/**
	 * Single Producer Single Consumer Queue
	 * 
	 * @return
	 */
	public static Builder singleProducer() {
		return new Builder(0);
	}

	/**
	 * Single Producer Single Consumer Queue
	 * 
	 * @param queueName
	 * @return
	 */
	public static Builder singleProducer(String queueName) {
		return new Builder(0).setQueueName(queueName);
	}

	/**
	 * Multiple Producer Single Consumer Queue
	 * 
	 * @return
	 */
	public static Builder multiProducer() {
		return new Builder(1);
	}

	/**
	 * Multiple Producer Single Consumer Queue
	 * 
	 * @param queueName
	 * @return
	 */
	public static Builder multiProducer(String queueName) {
		return new Builder(1).setQueueName(queueName);
	}

	protected JctSingleConsumerQueue(Consumer<E> consumer, int capacity, WaitingStrategy strategy) {
		super(consumer);
		this.queue = createQueue(capacity);
		this.strategy = strategy;
		this.runnable = () -> {
			try {
				while (isRunning.get() || !queue.isEmpty()) {
					E e = queue.poll();
					if (e != null)
						consumer.accept(e);
					else
						waiting();
				}
			} catch (Exception e) {
				throw new RuntimeException(queueName + " process thread throw exception", e);
			}
		};

	}

	protected abstract Queue<E> createQueue(int capacity);

	/**
	 * 
	 */
	private void waiting() {
		switch (strategy) {
		case SpinWaiting:
			break;
		case SleepWaiting:
			Threads.sleepIgnoreInterrupts(10);
			break;
		default:
			break;
		}
	}

	public boolean enqueue(E e) {
		if (isClosed.get()) {
			log.error("Queue -> {}, enqueue failure, This queue is closed", queueName);
			return false;
		}
		if (e == null) {
			log.error("Queue -> {}, enqueue element is null", queueName);
			return false;
		}
		while (!queue.offer(e))
			waiting();
		return true;
	}

	@Override
	protected void startProcessThread() {
		if (isRunning.compareAndSet(false, true)) {
			Threads.startNewMaxPriorityThread(queueName + "-ConsumerThread", runnable);
		} else {
			log.error("Queue -> {}, Error call, This queue is started", queueName);
			return;
		}
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	/**
	 * 
	 * @author yellow013
	 *
	 * @param <E>
	 * 
	 *            Single Producer Single Consumer Queue
	 * 
	 */
	private static final class JctSpscQueue<E> extends JctSingleConsumerQueue<E> {

		private static final Logger log = LoggerFactory.getLogger(JctSpscQueue.class);

		private JctSpscQueue(String queueName, int capacity, WaitingStrategy strategy, int mode, Consumer<E> consumer) {
			super(consumer, Math.max(capacity, 16), strategy);
			super.queueName = StringUtils.isEmpty(queueName) ? "JctSpscQueue-" + Threads.currentThreadName()
					: queueName;
			switch (mode) {
			case 1:
				start();
				break;
			case 0:
				log.info("Queue -> {} :: Run mode is [Manual], wating start...", super.queueName);
				break;
			}
		}

		@Override
		protected SpscArrayQueue<E> createQueue(int capacity) {
			return new SpscArrayQueue<>(capacity);
		}

	}

	/**
	 * 
	 * @author yellow013
	 *
	 * @param <E>
	 * 
	 *            Multiple Producer Single Consumer Queue
	 * 
	 */
	private static final class JctMpscQueue<E> extends JctSingleConsumerQueue<E> {

		private static final Logger log = LoggerFactory.getLogger(JctMpscQueue.class);

		private JctMpscQueue(String queueName, int capacity, WaitingStrategy strategy, int mode, Consumer<E> consumer) {
			super(consumer, Math.max(capacity, 16), strategy);
			super.queueName = StringUtils.isEmpty(queueName) ? "JctMpscQueue-" + Threads.currentThreadName()
					: queueName;
			switch (mode) {
			case 1:
				start();
				break;
			case 0:
				log.info("Queue -> {} :: Run mode is [Manual], wating start...", super.queueName);
				break;
			}
		}

		@Override
		protected Queue<E> createQueue(int capacity) {
			return new MpscArrayQueue<>(capacity);
		}

	}

	/**
	 * 
	 * JctQueue Builder
	 * 
	 * @author yellow013
	 */
	public static class Builder {

		// 0 == SPSC Queue; 1 == MPSC Queue
		private final int style;
		private String queueName = null;
		// 0 == Manual ; 1 == Auto
		private int startMode = 1;
		private WaitingStrategy strategy = WaitingStrategy.SpinWaiting;
		private int capacity = 32;

		private Builder(int style) {
			this.style = style;
		}

		public Builder setQueueName(String queueName) {
			this.queueName = queueName;
			return this;
		}

		public Builder setStartMode(int startMode) {
			this.startMode = startMode;
			return this;
		}

		public Builder setWaitingStrategy(WaitingStrategy strategy) {
			this.strategy = strategy;
			return this;
		}

		public Builder setCapacity(int capacity) {
			this.capacity = capacity;
			return this;
		}

		public final <E> JctSingleConsumerQueue<E> buildWithConsumer(Consumer<E> consumer) {
			switch (style) {
			case 0:
				return new JctSpscQueue<>(queueName, capacity, strategy, startMode, consumer);
			case 1:
				return new JctMpscQueue<>(queueName, capacity, strategy, startMode, consumer);
			default:
				throw new IllegalArgumentException("Error enum item");
			}
		}

	}

	public static void main(String[] args) {

	}

}
