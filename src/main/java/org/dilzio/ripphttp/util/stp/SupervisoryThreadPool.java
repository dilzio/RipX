package org.dilzio.ripphttp.util.stp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dilzio.riphttp.util.NotImplementedException;


public class SupervisoryThreadPool implements ExecutorService {
	private static final Logger LOG = LogManager.getFormatterLogger(SupervisoryThreadPool.class.getName());
	private static final int QUEUESIZE = 100; // TODO fix hack

	private final ExecutorService _internalPool;
	private final List<RunnableWrapper> _wrappedRunnables = new ArrayList<RunnableWrapper>();
	private Thread _execThread;
	private final BlockingQueue<RunnableWrapper> _execQ = new ArrayBlockingQueue<RunnableWrapper>(QUEUESIZE);

	public SupervisoryThreadPool(RestartPolicy restartPolicy, int numThreads, final Runnable... runnables) {
		ExceptionHandler handler = new ExceptionHandler(restartPolicy);
		_internalPool = Executors.newFixedThreadPool(numThreads, new DaemonThreadFactory(handler));
		for (Runnable r : runnables) {
			_wrappedRunnables.add(wrapRunnable(r));
		}
	}

	private void init() throws InterruptedException {
		_execThread = new Thread(new Runnable() {

			@Override
			public void run() {
				LOG.info("Starting execution thread on pool thread %s", Thread.currentThread().getName());
				while (!Thread.interrupted()) {
					try {
						RunnableWrapper rw = _execQ.take();
						_internalPool.execute(rw);
						LOG.info("RunnableWrapper %s resubmitted for execution.", rw.getName());
					} catch (InterruptedException ie) {
						LOG.info("Interrupted Exception in Exec Thread...exiting execThread.");
					}
				}
				LOG.warn("Stopping execution thread on pool thread %s", Thread.currentThread().getName());
			}

		});
		_execThread.setName("STP Exec Thread");
		_execThread.start();
	}

	public void join() throws InterruptedException {
		_internalPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

	public void execute(Runnable r) {
		if (null == _execThread) {
			try {
				init();
			} catch (InterruptedException e) {
				throw new RuntimeException("Unable to init exec thread", e);
			}
		}
		executeInternal(wrapRunnable(r));
	}

	private void executeInternal(RunnableWrapper rw) {
		LOG.debug("Executing wrapped runnable: %s", rw.getName());
		_internalPool.execute(rw);
	}

	private RunnableWrapper wrapRunnable(Runnable r) {
		return new RunnableWrapper(r);
	}

	@Override
	public void shutdown() {
		_internalPool.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return _internalPool.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return _internalPool.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return _internalPool.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return _internalPool.awaitTermination(timeout, unit);
	}

	// Unimplemented ExecutorService Methods
	@Override
	public <T> Future<T> submit(Callable<T> task) {
		throw new NotImplementedException();
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		throw new NotImplementedException();
	}

	@Override
	public Future<?> submit(Runnable task) {
		throw new NotImplementedException();
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		throw new NotImplementedException();
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
		throw new NotImplementedException();
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		throw new NotImplementedException();
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		throw new NotImplementedException();
	}
}
