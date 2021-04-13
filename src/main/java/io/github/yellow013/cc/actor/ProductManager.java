package io.github.yellow013.cc.actor;

import java.util.concurrent.atomic.AtomicInteger;

public final class ProductManager {

	public static final ProductManager INSTANCE = new ProductManager();

	// 提供自增编号
	private static final AtomicInteger i = new AtomicInteger();

	private final String name;

	private ProductManager() {
		this.name = "ProductManager[" + i.incrementAndGet() + "]";
	}

	public String getName() {
		return name;
	}

}
