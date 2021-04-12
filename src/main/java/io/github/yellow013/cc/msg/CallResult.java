package io.github.yellow013.cc.msg;

import io.github.yellow013.cc.util.EpochSequence;

public class CallResult {

	private final long seq;
	private final long processedSeq;
	// 1 == success
	private final int status;
	private final String result;

	/**
	 * 
	 * @param processedSeq
	 * @param status
	 * @param result
	 */
	public CallResult(long processedSeq, int status, String result) {
		this.seq = EpochSequence.allocate();
		this.processedSeq = processedSeq;
		this.status = status;
		this.result = result;
	}

	public long getSeq() {
		return seq;
	}

	public long getProcessedSeq() {
		return processedSeq;
	}

	public int getStatus() {
		return status;
	}

	public String getResult() {
		return result;
	}

}
