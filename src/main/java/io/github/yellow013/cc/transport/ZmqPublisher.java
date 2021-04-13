package io.github.yellow013.cc.transport;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import io.github.yellow013.cc.util.Threads;

/**
 * 
 * @see <a>https://github.com/yellow013/mercury/blob/master/transport/transport-zmq/src/main/java/io/mercury/transport/zmq/ZmqPublisher.java</a>
 * 
 * @author yellow013
 * 
 * @param <T>
 */
public final class ZmqPublisher<T> extends ZmqTransport implements Closeable {

	// default topic
	private final String defaultTopic;

	private final String name;

	/**
	 * 消息序列化
	 */
	private final Function<T, byte[]> ser;

	private static final Logger log = LoggerFactory.getLogger(ZmqPublisher.class);

	/**
	 * 
	 * @param cfg
	 * @param ser
	 */
	private ZmqPublisher(ZmqPubConfigurator cfg, Function<T, byte[]> ser) {
		super(cfg.addr, cfg.ioThreads);
		this.ser = ser;
		this.defaultTopic = cfg.defaultTopic;
		if (socket.bind(addr)) {
			log.info("bound addr -> {}", addr);
		} else {
			log.error("unable to bind -> {}", addr);
			throw new RuntimeException("unable to connect addr -> " + addr);
		}
		setTcpKeepAlive(cfg.keepAlive, cfg.keepAliveCount, cfg.keepAliveIdle, cfg.keepAliveInterval);
		this.name = "zmq::pub$" + addr + "/" + defaultTopic;
	}

	/**
	 * 
	 * @param cfg
	 * @return
	 */
	public static ZmqPublisher<String> create(ZmqPubConfigurator cfg) {
		return create(cfg, Charset.defaultCharset());
	}

	/**
	 * 
	 * @param cfg
	 * @param charset
	 * @return
	 */
	public static ZmqPublisher<String> create(ZmqPubConfigurator cfg, Charset charset) {
		return new ZmqPublisher<>(cfg, str -> str.getBytes(charset));
	}

	/**
	 * 
	 * @param <T>
	 * @param cfg
	 * @param ser
	 * @return
	 */
	public static <T> ZmqPublisher<T> create(ZmqPubConfigurator cfg, Function<T, byte[]> ser) {
		return new ZmqPublisher<>(cfg, ser);
	}

	@Override
	protected SocketType getSocketType() {
		return SocketType.PUB;
	}

	public String getName() {
		return name;
	}

	public void publish(T msg) {
		publish(defaultTopic, msg);
	}

	public void publish(String target, T msg) {
		if (isRunning.get()) {
			byte[] bytes = ser.apply(msg);
			if (bytes != null && bytes.length > 0) {
				socket.sendMore(target);
				socket.send(bytes, ZMQ.NOBLOCK);
			}
		} else {
			log.warn("ZmqPublisher -> [{}] already closed", name);
		}
	}

	/**
	 * 
	 * @author yellow013
	 *
	 */
	public static final class ZmqPubConfigurator {

		private final String addr;

		private String defaultTopic = "";

		private int ioThreads = 1;

		private int keepAlive = -1;
		private int keepAliveCount;
		private int keepAliveIdle;
		private int keepAliveInterval;

		private ZmqPubConfigurator(String addr) {
			this.addr = addr;
		}

		public ZmqPubConfigurator setDefaultTopic(String defaultTopic) {
			this.defaultTopic = defaultTopic;
			return this;
		}

		public ZmqPubConfigurator setIoThreads(int ioThreads) {
			this.ioThreads = ioThreads;
			return this;
		}

		public ZmqPubConfigurator setKeepAlive(int keepAlive) {
			this.keepAlive = keepAlive;
			return this;
		}

		public ZmqPubConfigurator setKeepAliveCount(int keepAliveCount) {
			this.keepAliveCount = keepAliveCount;
			return this;
		}

		public ZmqPubConfigurator setKeepAliveIdle(int keepAliveIdle) {
			this.keepAliveIdle = keepAliveIdle;
			return this;
		}

		public ZmqPubConfigurator setKeepAliveInterval(int keepAliveInterval) {
			this.keepAliveInterval = keepAliveInterval;
			return this;
		}

		/**
		 * 创建TCP协议连接
		 * 
		 * @param port
		 * @return
		 */
		public final static ZmqPubConfigurator tcp(int port) {
			return tcp("*", port);
		}

		/**
		 * 创建TCP协议连接
		 * 
		 * @param addr
		 * @param port
		 * @return
		 */
		public final static ZmqPubConfigurator tcp(String addr, int port) {
			return new ZmqPubConfigurator("tcp://" + addr + ":" + port);
		}

		/**
		 * 创建IPC协议连接
		 * 
		 * @param addr
		 * @return
		 */
		public final static ZmqPubConfigurator ipc(String addr) {
			return new ZmqPubConfigurator("ipc://" + addr);
		}

	}

	public static void main(String[] args) {

		ZmqPubConfigurator configurator = ZmqPubConfigurator.tcp("127.0.0.1", 13001).setDefaultTopic("command")
				.setIoThreads(2);

		try (ZmqPublisher<String> publisher = ZmqPublisher.create(configurator)) {
			Random random = new Random();

			for (;;) {
				publisher.publish(String.valueOf(random.nextInt()));
				Threads.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
