package io.github.yellow013.cc.transport;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;

/**
 * 
 * @see <a>https://github.com/yellow013/mercury/blob/master/transport/transport-zmq/src/main/java/io/mercury/transport/zmq/ZmqSubscriber.java</a>
 * 
 * @author yellow013
 *
 */
public final class ZmqSubscriber extends ZmqTransport implements Closeable, Runnable {

	// topics
	private final String[] topics;

	private final String name;

	/**
	 * 订阅消息消费者
	 */
	private final BiConsumer<byte[], byte[]> consumer;

	private static final Logger log = LoggerFactory.getLogger(ZmqSubscriber.class);

	private ZmqSubscriber(Builder builder) {
		super(builder.addr, builder.ioThreads);
		if (builder.consumer == null) {
			log.info("consumer can not null");
			throw new NullPointerException("consumer can not null");
		}
		this.consumer = builder.consumer;
		this.topics = builder.topics;
		if (socket.connect(addr)) {
			log.info("connected addr -> {}", addr);
		} else {
			log.error("unable to connect addr -> {}", addr);
			throw new RuntimeException("unable to connect addr -> " + addr);
		}
		setTcpKeepAlive(builder.keepAlive, builder.keepAliveCount, builder.keepAliveIdle, builder.keepAliveInterval);
		for (String topic : topics) {
			socket.subscribe(topic.getBytes(Charset.defaultCharset()));
		}
		this.name = "zmq::sub$" + builder.addr + "/" + topics;
	}

	/**
	 * 创建TCP协议连接
	 * 
	 * @param port
	 * @return
	 */
	public final static Builder tcp(int port) {
		return tcp("*", port);
	}

	/**
	 * 创建TCP协议连接
	 * 
	 * @param addr
	 * @param port
	 * @return
	 */
	public final static Builder tcp(String addr, int port) {
		return new Builder("tcp://" + addr + ":" + port);
	}

	/**
	 * 创建IPC协议连接
	 * 
	 * @param addr
	 * @return
	 */
	public final static Builder ipc(String addr) {
		return new Builder("ipc://" + addr);
	}

	@Override
	protected SocketType getSocketType() {
		return SocketType.SUB;
	}

	public String getName() {
		return name;
	}

	/**
	 * 
	 * @author yellow013
	 *
	 */
	public static class Builder {

		private final String addr;

		private String[] topics = new String[] { "" };

		private int ioThreads = 1;

		private int keepAlive = -1;
		private int keepAliveCount;
		private int keepAliveIdle;
		private int keepAliveInterval;

		private BiConsumer<byte[], byte[]> consumer;

		private Builder(String addr) {
			this.addr = addr;
		}

		public Builder setIoThreads(int ioThreads) {
			this.ioThreads = ioThreads;
			return this;
		}

		public Builder setKeepAlive(int keepAlive) {
			this.keepAlive = keepAlive;
			return this;
		}

		public Builder setKeepAliveCount(int keepAliveCount) {
			this.keepAliveCount = keepAliveCount;
			return this;
		}

		public Builder setKeepAliveIdle(int keepAliveIdle) {
			this.keepAliveIdle = keepAliveIdle;
			return this;
		}

		public Builder setKeepAliveInterval(int keepAliveInterval) {
			this.keepAliveInterval = keepAliveInterval;
			return this;
		}

		public Builder setTopics(String... topics) {
			this.topics = topics;
			return this;
		}

		/**
		 * 在构建时定义如何处理接收到的Topic和Content
		 * 
		 * @param consumer
		 * @return
		 */
		public ZmqSubscriber build(BiConsumer<byte[], byte[]> consumer) {
			this.consumer = consumer;
			return new ZmqSubscriber(this);
		}

	}

	public void subscribe() {
		while (isRunning.get()) {
			byte[] topic = socket.recv();
			log.debug("received topic bytes, length: {}", topic.length);
			byte[] msg = socket.recv();
			log.debug("received msg bytes, length: {}", topic.length);
			consumer.accept(topic, msg);
		}
		log.warn("ZmqSubscriber -> [{}] already closed", name);
	}

	public void reconnect() {
		throw new UnsupportedOperationException("ZmqSubscriber unsupport reconnect");
	}

	@Override
	public void run() {
		subscribe();
	}

	public static void main(String[] args) {

		try (ZmqSubscriber subscriber = ZmqSubscriber.tcp("127.0.0.1", 5556).setTopics("call").setIoThreads(2)
				.build((topic, msg) -> System.out.println(new String(topic) + "->" + new String(msg)))) {
			subscriber.subscribe();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
