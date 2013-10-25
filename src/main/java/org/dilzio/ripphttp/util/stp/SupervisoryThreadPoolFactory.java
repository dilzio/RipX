package org.dilzio.ripphttp.util.stp;

import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SupervisoryThreadPoolFactory {
	private static final Logger LOG = LogManager.getFormatterLogger(SupervisoryThreadPoolFactory.class.getName());
	private static final SupervisoryThreadPoolFactory INSTANCE = new SupervisoryThreadPoolFactory(); 

	public ExecutorService newOneForOneSupervisoryThreadPool(final int maxRestarts, final long restartWindowMillis, 
															 final int numThreads, Runnable ... runnables) {
		
		OneForOneRestartPolicy policy = new OneForOneRestartPolicy(maxRestarts, restartWindowMillis);
		SupervisoryThreadPool pool =  new SupervisoryThreadPool(policy, numThreads, runnables);
		policy.setExecutorService(pool);
		LOG.info("Configured SupoervisoryThreadPool with OneForOneRestartPolicy. Params are: numThreads: %s, maxRestarts: %s, restartWindowMillis: %s, runnable list size: %s",
				 numThreads, maxRestarts, restartWindowMillis, runnables.length);
		return pool;
	}
	
	public static SupervisoryThreadPoolFactory getInstance() {
		return INSTANCE;
	}
}
