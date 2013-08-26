/*
 * (c) 2013 nobrooklyn
 */
package com.github.chanko.mbean;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter implements CounterMBean {
	private AtomicInteger counter = new AtomicInteger(0);

	@Override
	public int getCount() {
		return counter.get();
	}

	@Override
	public void increment() {
		counter.incrementAndGet();
	}
}
