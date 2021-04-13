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

	private long seq;
	
	// [0, 1, 2, 3, 4, 5, 6] Employees handle
	// [7, 8] Technical Leader handle
	// [all] Product Manager handle
	private int envelope;
	
	private String msg;

	public Call() {
	}

	public Call(int envelope, String msg) {
		this.seq = EpochSequence.allocate();
		this.envelope = envelope;
		this.msg = msg;
	}

	public Call setSeq(long seq) {
		if (this.seq == 0)
			this.seq = seq;
		return this;
	}

	public Call setEnvelope(int envelope) {
		this.envelope = envelope;
		return this;
	}

	public Call setMsg(String msg) {
		this.msg = msg;
		return this;
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
