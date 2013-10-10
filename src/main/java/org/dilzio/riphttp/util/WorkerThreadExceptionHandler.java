package org.dilzio.riphttp.util;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkerThreadExceptionHandler implements UncaughtExceptionHandler {

	private static final Logger LOG = LogManager.getFormatterLogger(WorkerThreadExceptionHandler.class.getName());

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LOG.error("Uncaught exception thread %s message was: %s", t.getName(), e.getMessage());
	}

}
