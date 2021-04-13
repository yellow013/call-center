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

	// 第一组占位
	protected long p0, p1, p2, p3, p4, p5, p6;
	// 最后使用的Epoch毫秒
	private volatile long lastEpochMillis;
	// 第二组占位
	protected long p7, p8, p9, p10, p11, p12, p13;
	// 自增位
	private volatile long incr;
	// 第三组占位
	protected long p14, p15, p16, p17, p18, p19, p20;

	private EpochSequence() {
	}

	/**
	 * 
	 * 
	 * <pre>
	 * 0b|-----------------epoch milliseconds ----------------|---increment----|
	 * 0b01111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111
	 * 
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
		// 如果当前时间小于上一次ID生成的时间戳, 说明系统时钟回退过这个时候应当抛出异常
		if (currentEpochMillis < lastEpochMillis) {
			throw new RuntimeException("The clock moved backwards, Refusing to generate seq for "
					+ (lastEpochMillis - currentEpochMillis) + " millis");
		}
		// 如果是同一时间生成的, 则进行毫秒内序列
		if (currentEpochMillis == lastEpochMillis) {
			incr = (incr + 1) & IncrMask;
			// 毫秒内序列溢出
			if (incr == 0L) {
				// 阻塞到下一个毫秒, 获得新的时间戳
				currentEpochMillis = nextMillis(lastEpochMillis);
			}
		}
		// 时间戳改变, 毫秒内序列重置
		else {
			incr = 0L;
		}
		// 更新最后一次生成ID的时间截
		lastEpochMillis = currentEpochMillis;
		return // 计算最终的序列
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
