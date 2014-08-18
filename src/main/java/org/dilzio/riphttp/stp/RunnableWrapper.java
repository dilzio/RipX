package org.dilzio.riphttp.stp;

import java.util.concurrent.atomic.AtomicInteger;

public class RunnableWrapper implements Runnable {
	private final static AtomicInteger COUNTER = new AtomicInteger();
	private final Runnable _wrappedRunnable;

	private final String _name;
	
	public RunnableWrapper(final Runnable wrappedRunnable) {
		_wrappedRunnable = wrappedRunnable;
		_name = "RW=" + COUNTER.getAndIncrement();
	}

	@Override
	public void run() {
		RunnableWrapperThreadLocal.getInstance().set(this);
		_wrappedRunnable.run();
		RunnableWrapperThreadLocal.getInstance().unset();
	}

	public String getName() {
		return _name;
	}
}
