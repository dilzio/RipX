package org.dilzio.riphttp.stp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler implements UncaughtExceptionHandler {
	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class.getName());
	private final RestartPolicy _restartPolicy;
	
	public ExceptionHandler(final RestartPolicy restartPolicy) {
		_restartPolicy = restartPolicy;
	}

	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		try{
			LOG.error("Uncaught exception " + e + " on thread " + t.getName() + ".  Handing to restart policy", e );
			RunnableWrapper rw = RunnableWrapperThreadLocal.getInstance().get();
			_restartPolicy.apply(rw);
		}finally{
			RunnableWrapperThreadLocal.getInstance().unset();
		}
	}
}
