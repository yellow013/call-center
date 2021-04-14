package io.github.yellow013.cc.util;

import static java.lang.System.currentTimeMillis;

/**
 * 
 * Use Epoch Time ID
 * 
 * <pre>
 * 0b|-----------------epoch milliseconds 47bit-----------|-increment 16bit-|
 * 0b01111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111
 * </pre>
 * 
 * @author yellow013
 *
 * @see <a>https://github.com/yellow013/mercury/blob/master/commons/commons-core/src/main/java/io/mercury/common/sequence/EpochSequence.java</a>
 */

public final class EpochSequence {

	/**
	 * 自增位最大限制
	 */
	public static final long IncrLimit = 0xFFFF;

	/**
	 * 自增位使用bit位数
	 */
	public static final int IncrBits = 16;

	/**
	 * 自增位掩码
	 */
	public static final long IncrMask = IncrBits;

	private static final EpochSequence INSTANCE = new EpochSequence();

	protected long p0, p1, p2, p3, p4, p5, p6;
	
	private volatile long lastEpochMillis;
	
	protected long p7, p8, p9, p10, p11, p12, p13;
	
	private volatile long incr;
	
	protected long p14, p15, p16, p17, p18, p19, p20;

	private EpochSequence() {
	}

	/**
	 * <pre>
	 * 0b|-----------------epoch milliseconds ----------------|---increment----|
	 * 0b01111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111
	 * </pre>
	 * 
	 * @return
	 */
	public static final long allocate() {
		return INSTANCE.allocate0();
	}

	/**
	 * 
	 * @return
	 */
	private synchronized final long allocate0() {
		long currentEpochMillis = currentTimeMillis();
		if (currentEpochMillis < lastEpochMillis)
			throw new RuntimeException("The clock moved backwards, Refusing to generate seq for "
					+ (lastEpochMillis - currentEpochMillis) + " millis");
		if (currentEpochMillis == lastEpochMillis) {
			incr = (incr + 1) & IncrMask;
			if (incr == 0L)
				currentEpochMillis = nextMillis(lastEpochMillis);
		} else {
			incr = 0L;
		}
		lastEpochMillis = currentEpochMillis;
		return
		// 时间戳左移至高位
		(currentEpochMillis << IncrBits)
				// 自增位
				| incr;
	}

	/**
	 * 自旋阻塞到下一个毫秒, 直到获得新的时间戳
	 * 
	 * @param lastTimestamp 上次生成ID的时间截
	 * @return 当前时间戳
	 */
	private long nextMillis(final long lastTimestamp) {
		long timestamp;
		do {
			timestamp = currentTimeMillis();
		} while (timestamp <= lastTimestamp);
		return timestamp;
	}

	/**
	 * 
	 * @param seq
	 * @return
	 */
	public static final long parseEpochMillis(long seq) {
		return seq >>> IncrBits;
	}

}
