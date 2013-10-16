package org.dilzio.ripphttp.util.stp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OneForOneRestartPolicy implements RestartPolicy {

	private static final Logger LOG = LogManager.getFormatterLogger(OneForOneRestartPolicy.class.getName());
	private static ConcurrentMap<RunnableWrapper, AtomicInteger> _restartCountMap = new ConcurrentHashMap<RunnableWrapper, AtomicInteger>();
	private final int _maxRestarts;
	private final BlockingQueue<RunnableWrapper> _execQ;

	public OneForOneRestartPolicy(final BlockingQueue<RunnableWrapper> execQ, final int maxRestarts) {
		_execQ = execQ;
		_maxRestarts = maxRestarts;
	}

	@Override
	public void apply(ExecutorService _internalPool, ConcurrentMap<Thread, RunnableWrapper> _internalMap) {
		RunnableWrapper rw = _internalMap.get(Thread.currentThread());
		AtomicInteger counterForRunnable = null;
		if (_restartCountMap.containsKey(rw)) {
			counterForRunnable = _restartCountMap.get(rw);
		} else {
			counterForRunnable = new AtomicInteger();
			counterForRunnable.incrementAndGet(); // this is first pass
			_restartCountMap.put(rw, counterForRunnable);
		}

		if (counterForRunnable.get() < _maxRestarts) {
			counterForRunnable.incrementAndGet();
			_internalPool.execute(rw);
			LOG.warn("Respawned runnable %s on new thread", rw.getName());
		} else {
			LOG.error("Exceeded max restarts for runnable %s", rw.getName());
		}
	}

@Override
	public void apply2(final RunnableWrapper rw) {
		AtomicInteger counterForRunnable = null;
		if (_restartCountMap.containsKey(rw)) {
			counterForRunnable = _restartCountMap.get(rw);
		} else {
			counterForRunnable = new AtomicInteger();
			counterForRunnable.incrementAndGet(); // this is first pass
			_restartCountMap.put(rw, counterForRunnable);
		}

		if (counterForRunnable.get() < _maxRestarts) {
			counterForRunnable.incrementAndGet();
			try {
				_execQ.put(rw);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOG.error("Unable to respawn wrapped runnable %s", rw.getName());
			}
			LOG.warn("Respawned runnable %s on new thread", rw.getName());
		} else {
			LOG.error("Exceeded max restarts for runnable %s", rw.getName());
		}
	}
}