package io.github.yellow013.cc.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class CommonThreadPool extends ThreadPoolExecutor {

	private BiConsumer<Thread, Runnable> beforeHandler;
	private boolean hasBeforeHandle = false;

	private BiConsumer<Runnable, Throwable> afterHandler;
	private boolean hasAfterHandle = false;

	private String threadPoolName;

	private static final Logger log = LoggerFactory.getLogger(CommonThreadPool.class);

	private CommonThreadPool(String threadPoolName, ThreadPoolBuilder builder,
			BiConsumer<Thread, Runnable> beforeHandler, BiConsumer<Runnable, Throwable> afterHandler) {
		super(builder.corePoolSize, builder.maximumPoolSize, builder.keepAliveTime, builder.timeUnit,
				builder.workQueue);
		init(threadPoolName, beforeHandler, afterHandler);
	}

	private CommonThreadPool(String threadPoolName, ThreadPoolBuilder builder, ThreadFactory threadFactory,
			BiConsumer<Thread, Runnable> beforeHandler, BiConsumer<Runnable, Throwable> afterHandler) {
		super(builder.corePoolSize, builder.maximumPoolSize, builder.keepAliveTime, builder.timeUnit, builder.workQueue,
				threadFactory);
		init(threadPoolName, beforeHandler, afterHandler);
	}

	private CommonThreadPool(String threadPoolName, ThreadPoolBuilder builder, RejectedExecutionHandler rejectedHandler,
			BiConsumer<Thread, Runnable> beforeHandler, BiConsumer<Runnable, Throwable> afterHandler) {
		super(builder.corePoolSize, builder.maximumPoolSize, builder.keepAliveTime, builder.timeUnit, builder.workQueue,
				rejectedHandler);
		init(threadPoolName, beforeHandler, afterHandler);
	}

	private CommonThreadPool(String threadPoolName, ThreadPoolBuilder builder, ThreadFactory threadFactory,
			RejectedExecutionHandler rejectedHandler, BiConsumer<Thread, Runnable> beforeHandler,
			BiConsumer<Runnable, Throwable> afterHandler) {
		super(builder.corePoolSize, builder.maximumPoolSize, builder.keepAliveTime, builder.timeUnit, builder.workQueue,
				threadFactory, rejectedHandler);
		init(threadPoolName, beforeHandler, afterHandler);
	}

	private void init(String threadPoolName, BiConsumer<Thread, Runnable> beforeHandler,
			BiConsumer<Runnable, Throwable> afterHandler) {
		this.threadPoolName = threadPoolName;
		if (beforeHandler != null) {
			this.beforeHandler = beforeHandler;
			this.hasBeforeHandle = true;
		}
		if (afterHandler != null) {
			this.afterHandler = afterHandler;
			this.hasAfterHandle = true;
		}
	}

	public static ThreadPoolBuilder newBuilder() {
		return new ThreadPoolBuilder();
	}

	@Override
	protected void beforeExecute(Thread thread, Runnable runnable) {
		log.debug("Thread name -> {}, execute before", thread.getName());
		if (hasBeforeHandle)
			beforeHandler.accept(thread, runnable);
	}

	@Override
	protected void afterExecute(Runnable runnable, Throwable throwable) {
		log.debug("Throwable -> {}, execute after", throwable.getMessage());
		if (hasAfterHandle)
			afterHandler.accept(runnable, throwable);
	}

	@Override
	protected void terminated() {
		log.info("CommonThreadPool {} is terminated", threadPoolName);
	}

	public String threadPoolName() {
		return threadPoolName;
	}

	public final static class ThreadPoolBuilder {

		private int corePoolSize = Runtime.getRuntime().availableProcessors();
		private int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 4;
		private long keepAliveTime = 60;
		private TimeUnit timeUnit = TimeUnit.SECONDS;
		private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
		private ThreadFactory threadFactory;
		private RejectedExecutionHandler rejectedHandler;

		private BiConsumer<Thread, Runnable> beforeHandler;
		private BiConsumer<Runnable, Throwable> afterHandler;

		public ThreadPoolBuilder setCorePoolSize(int corePoolSize) {
			this.corePoolSize = corePoolSize;
			return this;
		}

		public ThreadPoolBuilder setMaximumPoolSize(int maximumPoolSize) {
			this.maximumPoolSize = maximumPoolSize;
			return this;
		}

		public ThreadPoolBuilder setKeepAliveTime(long keepAliveTime) {
			this.keepAliveTime = keepAliveTime;
			return this;
		}

		public ThreadPoolBuilder setTimeUnit(TimeUnit timeUnit) {
			this.timeUnit = timeUnit;
			return this;
		}

		public ThreadPoolBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
			this.workQueue = workQueue;
			return this;
		}

		public ThreadPoolBuilder setThreadFactory(ThreadFactory threadFactory) {
			this.threadFactory = threadFactory;
			return this;
		}

		public ThreadPoolBuilder setRejectedHandler(RejectedExecutionHandler rejectedHandler) {
			this.rejectedHandler = rejectedHandler;
			return this;
		}

		public ThreadPoolBuilder setBeforeHandler(BiConsumer<Thread, Runnable> beforeHandler) {
			this.beforeHandler = beforeHandler;
			return this;
		}

		public ThreadPoolBuilder setAfterHandler(BiConsumer<Runnable, Throwable> afterHandler) {
			this.afterHandler = afterHandler;
			return this;
		}

		public ThreadPoolExecutor build() {
			return build(null);
		}

		public ThreadPoolExecutor build(String threadPoolName) {
			threadPoolName = StringUtils.isEmpty(threadPoolName)
					? "CommonThreadPool-" + Math.abs(ThreadLocalRandom.current().nextInt())
					: threadPoolName;
			if (threadFactory != null && rejectedHandler != null) {
				return new CommonThreadPool(threadPoolName, this, threadFactory, rejectedHandler, beforeHandler,
						afterHandler);
			}
			if (threadFactory != null && rejectedHandler == null) {
				return new CommonThreadPool(threadPoolName, this, threadFactory, beforeHandler, afterHandler);
			}
			if (threadFactory == null && rejectedHandler != null) {
				return new CommonThreadPool(threadPoolName, this, rejectedHandler, beforeHandler, afterHandler);
			} else {
				return new CommonThreadPool(threadPoolName, this, beforeHandler, afterHandler);
			}
		}

	}

	public static void main(String[] args) {
		int COUNT_BITS = Integer.SIZE - 3;
		System.out.println(Integer.toBinaryString(-1 << COUNT_BITS));
		System.out.println(Integer.toBinaryString(0 << COUNT_BITS));
		System.out.println(Integer.toBinaryString(1 << COUNT_BITS));
		System.out.println(Integer.toBinaryString(2 << COUNT_BITS));
		System.out.println(Integer.toBinaryString(3 << COUNT_BITS));

	}

}
