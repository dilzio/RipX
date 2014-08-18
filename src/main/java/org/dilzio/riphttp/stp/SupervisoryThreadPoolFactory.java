package org.dilzio.riphttp.stp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class SupervisoryThreadPoolFactory {
	private static final Logger LOG = LoggerFactory.getLogger(SupervisoryThreadPoolFactory.class.getName());
	private static final SupervisoryThreadPoolFactory INSTANCE = new SupervisoryThreadPoolFactory(); 

	public ExecutorService newOneForOneSupervisoryThreadPool(final int maxRestarts, final long restartWindowMillis, 
															 final int numThreads, final String workerThreadNamePrefix) {

		OneForOneRestartPolicy policy = new OneForOneRestartPolicy(maxRestarts, restartWindowMillis);
		SupervisoryThreadPool pool =  new SupervisoryThreadPool(policy, numThreads, workerThreadNamePrefix);
		policy.setExecutorService(pool);
		LOG.info("Configured SupervisoryThreadPool with OneForOneRestartPolicy. Params are: numThreads: " + numThreads
                                                                                                          + ", maxRestarts:" + maxRestarts
                                                                                                          + ", restartWindowMillis: " + restartWindowMillis);
		return pool;
	}
	
	public static SupervisoryThreadPoolFactory getInstance() {
		return INSTANCE;
	}
}
