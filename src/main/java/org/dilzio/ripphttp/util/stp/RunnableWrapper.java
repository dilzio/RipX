package org.dilzio.ripphttp.util.stp;

import java.util.concurrent.ConcurrentMap;

public class RunnableWrapper implements Runnable {

	private final ConcurrentMap<Thread, RunnableWrapper> _runnableMap;
	private final Runnable _wrappedRunnable;

	private String _name = null;
	
	public RunnableWrapper(final ConcurrentMap<Thread, RunnableWrapper> threadToRunnableMap, final Runnable wrappedRunnable) {
		_runnableMap = threadToRunnableMap;
		_wrappedRunnable = wrappedRunnable;
	}

	@Override
	public void run() {
		Thread t = Thread.currentThread();
		_runnableMap.put(t, this); // associate this runnable with the current thread
		_name = t.getName();
		_wrappedRunnable.run();
		_runnableMap.remove(t);
	}

	public String getName() {
		return _name;
	}
}
