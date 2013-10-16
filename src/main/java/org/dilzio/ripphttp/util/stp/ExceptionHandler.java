package org.dilzio.ripphttp.util.stp;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExceptionHandler implements UncaughtExceptionHandler {
	private static final Logger LOG = LogManager.getFormatterLogger(ExceptionHandler.class.getName());
	private final RestartPolicy _restartPolicy;
	
	public ExceptionHandler(final RestartPolicy restartPolicy) {
		_restartPolicy = restartPolicy;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		try{
			LOG.error("Uncaught exception %s on thread %s. Handing to restart policy", e, t.getName());
			e.printStackTrace();
			RunnableWrapper rw = RunnableWrapperThreadLocal.getInstance().get();
			_restartPolicy.apply2(rw);
		}finally{
			RunnableWrapperThreadLocal.getInstance().unset();
		}
	}
}
