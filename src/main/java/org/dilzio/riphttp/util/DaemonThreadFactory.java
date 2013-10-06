package org.dilzio.riphttp.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory implements ThreadFactory {
	private static volatile int _instanceCounter = 0;
	
	public Thread newThread(Runnable r) {
		Thread t = Executors.defaultThreadFactory().newThread(r); 
		t.setDaemon(true);
		t.setName("thread-" + _instanceCounter);
		_instanceCounter = _instanceCounter += 1;
		return t;
	}
}
