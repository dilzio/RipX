package org.dilzio.ripphttp.util.stp;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public class ExceptionHandler implements UncaughtExceptionHandler {
	private final RestartPolicy _restartPolicy;
	private final ConcurrentMap<Thread, RunnableWrapper> _internalMap;
	private ExecutorService _internalPool;
	
	
	public ExceptionHandler(final RestartPolicy restartPolicy, final ConcurrentMap<Thread, RunnableWrapper> map ) {
		_restartPolicy = restartPolicy;
		_internalMap = map;
	}

	public void setExecutorService(ExecutorService es){
		_internalPool = es;
	}
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		try{
			_restartPolicy.apply(_internalPool,  _internalMap);
		}finally{
			_internalMap.remove(Thread.currentThread());
		}
	}
}
