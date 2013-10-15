package org.dilzio.ripphttp.util.stp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SupervisoryThreadPool {
	private static final Logger LOG = LogManager.getFormatterLogger(SupervisoryThreadPool.class.getName());
	private final ExecutorService _internalPool;
	private final List<RunnableWrapper> _wrappedRunnables = new ArrayList<RunnableWrapper>();
	private final ConcurrentMap<Thread, RunnableWrapper> _threadToRunnableMap = new ConcurrentHashMap<Thread, RunnableWrapper>();

	public SupervisoryThreadPool(final RestartPolicy restartPolicy, final Runnable... runnables) {
		ExceptionHandler handler = new ExceptionHandler(restartPolicy, _threadToRunnableMap);
		_internalPool = Executors.newCachedThreadPool(new DaemonThreadFactory(handler));
		handler.setExecutorService(_internalPool);
		for (Runnable r : runnables) {
			_wrappedRunnables.add(new RunnableWrapper(_threadToRunnableMap, r));
		}
		LOG.info("Added %s runnables", _wrappedRunnables.size());
	}

	public void start(){
		for (RunnableWrapper rw : _wrappedRunnables){
			_internalPool.execute(rw);
		}
	}
	
	




}
