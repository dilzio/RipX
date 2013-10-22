package org.dilzio.ripphttp.util.stp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dilzio.riphttp.util.Pair;

public class OneForOneRestartPolicy implements RestartPolicy {

	private static final Logger LOG = LogManager.getFormatterLogger(OneForOneRestartPolicy.class.getName());
	private static ConcurrentMap<RunnableWrapper, Pair<AtomicInteger, Long>> _restartCountMap = new ConcurrentHashMap<RunnableWrapper, Pair<AtomicInteger, Long>>();
	private final int _maxRestarts;
	private final BlockingQueue<RunnableWrapper> _execQ;
	private final long _restartWindowMillis;

	public OneForOneRestartPolicy(final BlockingQueue<RunnableWrapper> execQ, final int maxRestarts, final long restartWindowMillis) {
		_execQ = execQ;
		_maxRestarts = maxRestarts;
		_restartWindowMillis = restartWindowMillis;
	}

	@Override
	public void apply(final RunnableWrapper rw) {
		AtomicInteger counterForRunnable = null;
		Long lastRestartTime = null;
		if (_restartCountMap.containsKey(rw)) {
			counterForRunnable = _restartCountMap.get(rw).first();
			lastRestartTime = _restartCountMap.get(rw).second();
		} else {
			counterForRunnable = new AtomicInteger();
			counterForRunnable.incrementAndGet(); // this is first pass
			lastRestartTime = System.currentTimeMillis();
			Pair<AtomicInteger, Long> pair = new Pair<AtomicInteger, Long>();
			pair.setFirst(counterForRunnable);
			pair.setSecond(lastRestartTime);
			_restartCountMap.put(rw, pair);
		}

		if (lastRestartWasInWindow(System.currentTimeMillis(), lastRestartTime)) {
			if (counterForRunnable.get() < _maxRestarts) {
				counterForRunnable.incrementAndGet();
				try {
					_execQ.put(rw);
					
				} catch (InterruptedException e) {
					LOG.error("Unable to respawn wrapped runnable %s", rw.getName());
				}
				LOG.warn("Respawned runnable %s on new thread", rw.getName());
			} else {
				LOG.error("Exceeded max restarts for runnable %s", rw.getName());
			}
		}else{
			counterForRunnable.set(0);
			lastRestartTime = System.currentTimeMillis();
		}

	}

	private boolean lastRestartWasInWindow(final long currentTimeMillis, final Long lastRestartTime) {
		if ((lastRestartTime - currentTimeMillis) < _restartWindowMillis){
			return true;
		}
		return false;
	}

}
