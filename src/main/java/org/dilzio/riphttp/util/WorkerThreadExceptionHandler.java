package org.dilzio.riphttp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;

public class WorkerThreadExceptionHandler implements UncaughtExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(WorkerThreadExceptionHandler.class.getName());

	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		LOG.error("Uncaught exception thread " + t.getName() + " message was: " +  e.getMessage());
	}

}
