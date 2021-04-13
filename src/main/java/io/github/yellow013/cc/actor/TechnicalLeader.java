package io.github.yellow013.cc.actor;

import java.util.concurrent.atomic.AtomicInteger;

public class TechnicalLeader {

	public static final TechnicalLeader INSTANCE = new TechnicalLeader();

	// 提供自增编号
	private static final AtomicInteger i = new AtomicInteger();

	private final String name;

	private TechnicalLeader() {
		this.name = "TechnicalLeader[" + i.incrementAndGet() + "]";
	}

	public String getName() {
		return name;
	}

}
