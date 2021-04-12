package io.github.yellow013.cc.transport;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * 
 * 
 * 
 * @see <a>https://github.com/yellow013/mercury/blob/master/transport/transport-zmq/src/main/java/io/mercury/transport/zmq/ZmqTransport.java</a>
 * 
 * @author yellow013
 *
 */
abstract class ZmqTransport implements Closeable {

	// ZContext
	protected ZContext ctx;

	// ZMQ.Socket
	protected ZMQ.Socket socket;

	protected final String addr;

	// 组件运行状态, 初始为已开始运行
	protected AtomicBoolean isRunning = new AtomicBoolean(true);

	private static final Logger log = LoggerFactory.getLogger(ZmqTransport.class);

	protected ZmqTransport(String addr, int ioThreads) {
		this.ctx = new ZContext(ioThreads);
		this.addr = addr;
		log.info("zmq context initialized, ioThreads=={}", ioThreads);
		SocketType type = getSocketType();
		this.socket = ctx.createSocket(type);
		log.info("zmq socket created with type -> {}", type);
	}

	protected abstract SocketType getSocketType();

	/**
	 * 设置TcpKeepAlive, 由子类调用
	 *
	 * @param option
	 * @return
	 */
	protected ZMQ.Socket setTcpKeepAlive(int keepAlive, int keepAliveCount, int keepAliveIdle, int keepAliveInterval) {
		if (keepAlive != -1) {
			log.info("setting zmq socket tcp keep alive");
			socket.setTCPKeepAlive(keepAlive);
			socket.setTCPKeepAliveCount(keepAliveCount);
			socket.setTCPKeepAliveIdle(keepAliveIdle);
			socket.setTCPKeepAliveInterval(keepAliveInterval);
		}
		return socket;
	}

	public boolean isConnected() {
		return !ctx.isClosed();
	}

	@Override
	public void close() throws IOException {
		destroy();
	}

	public boolean destroy() {
		if (isRunning.compareAndSet(true, false)) {
			socket.close();
			ctx.close();
		}
		log.info("zmq transport destroy");
		return ctx.isClosed();
	}

}
