package io.github.yellow013.cc.msg;

import io.github.yellow013.cc.util.EpochSequence;

/**
 * 
 * 这里用于定义一个呼叫的对象
 * 
 * @author yellow013
 *
 */
public class Call implements Comparable<Call> {

	private final long seq;
	private final int envelope;
	private final String msg;

	private Call(int envelope, String msg) {
		this.seq = EpochSequence.allocate();
		this.envelope = envelope;
		this.msg = msg;
	}

	public long getSeq() {
		return seq;
	}

	public int getEnvelope() {
		return envelope;
	}

	public String getMsg() {
		return msg;
	}

	@Override
	public int compareTo(Call o) {
		return seq < o.seq ? -1 : seq > o.seq ? 1 : 0;
	}

}
