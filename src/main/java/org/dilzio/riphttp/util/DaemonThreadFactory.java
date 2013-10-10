package org.dilzio.riphttp.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

import org.apache.http.util.Args;
public class DaemonThreadFactory implements ThreadFactory {
	private static volatile int _instanceCounter = 0;
	private final UncaughtExceptionHandler _exceptionHandler;

	public DaemonThreadFactory(final UncaughtExceptionHandler ueh){
		Args.notNull(ueh, "exception handler cannot be null");
		_exceptionHandler = ueh;
	}
	
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setUncaughtExceptionHandler(_exceptionHandler);
		t.setDaemon(true);
		t.setName("thread-" + _instanceCounter);
		_instanceCounter += 1;
		return t;
	}
}
