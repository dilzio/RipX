package org.dilzio.ripphttp.util.stp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dilzio.riphttp.util.Pair;

/**
 * Policy which will will respawn runnables on failed threads
 * Respawn will happen under the following circumstances:
 * <ol>
 *	<li> The currentTime - lastRestartTime is greater than the lookbackWindow.</li>
 *  <li>The currentTime - lastRestartTime is less than the lookbackWindow and the currentRestartCounter is less than the the maxRestartsInWindow parameter<li>
 * </ol>
 * 
 * Other Notes:
 * <ul>
 *	<li>If the maxRestartsInWindow and lookBackWindowMillis params are negative, the thread will always be respawned</li>
 *	<li>When a thread is not respawned an ErrorLog is written.</li>
 *	<li>Whenever a failure happens outside of the lookBackWindow, the restartCounter is reset to 1 for that runnable and the last restart time set to the current time.  The idea is to catch clustered failures, but not to permanently count failures over a long running process</li>
 * </ul>
 * @author dilzio
 *
 */
public class OneForOneRestartPolicy implements RestartPolicy {

	private static final Logger LOG = LogManager.getFormatterLogger(OneForOneRestartPolicy.class.getName());
	private static ConcurrentMap<RunnableWrapper, Pair<AtomicInteger, Long>> _restartCountMap = new ConcurrentHashMap<RunnableWrapper, Pair<AtomicInteger, Long>>();
	private final int _maxRestartsInWindow;
	private final long _lookbackWindowMillis;

	private ExecutorService _execService;
	OneForOneRestartPolicy(final int maxRestartsInWindow, final long lookbackWindowMillis) {
		_maxRestartsInWindow = maxRestartsInWindow;
		_lookbackWindowMillis = lookbackWindowMillis;
	}

	/**
	 * This needs to be set before the class runs. Really it should be a constructor based injection, but since there is a 
	 * circular dependency between SupervisoryThreadPool and OneForOneRestartPolicy (STP needs OFORP on properly set up its
	 * internal thread pool exception handler and OFORP uses STP to execute respawned threads), it sucks less to put a setter on
	 * this class rather than a setter for the restart policy on the STP.
	 * @param execService
	 */
	public void setExecutorService(final ExecutorService execService){
		_execService = execService;
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
			if (counterForRunnable.get() < _maxRestartsInWindow) {
				counterForRunnable.incrementAndGet();
				respawnRunnable(rw);
			} else {
				LOG.error("Exceeded max restarts for runnable %s", rw.getName());
			}
		}else{
			counterForRunnable.set(1);
			lastRestartTime = System.currentTimeMillis();
			respawnRunnable(rw);
		}

	}

	private void respawnRunnable(RunnableWrapper rw){
			_execService.execute(rw);
			LOG.warn("Respawned runnable %s on new thread", rw.getName());
	}
	private boolean lastRestartWasInWindow(final long currentTimeMillis, final Long lastRestartTime) {
		if ((currentTimeMillis - lastRestartTime) < _lookbackWindowMillis){
			return true;
		}
		return false;
	}

}
