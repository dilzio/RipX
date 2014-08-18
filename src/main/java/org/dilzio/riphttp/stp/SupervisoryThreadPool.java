package org.dilzio.riphttp.stp;

import org.dilzio.riphttp.util.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*; //NOPMD

@SuppressWarnings("PMD.TooManyMethods")
public class SupervisoryThreadPool implements ExecutorService {
    private static final Logger LOG = LoggerFactory.getLogger(SupervisoryThreadPool.class.getName());
    private static final int QUEUESIZE = 100000; // TODO fix hack

    private final ExecutorService _internalPool;
    private Thread _execThread;
    private final BlockingQueue<RunnableWrapper> _execQ = new ArrayBlockingQueue<>(QUEUESIZE);

    public SupervisoryThreadPool(final RestartPolicy restartPolicy, final int numThreads, final String workerThreadNamePrefix) {
        ExceptionHandler handler = new ExceptionHandler(restartPolicy);
        _internalPool = new ThreadPoolExecutor(numThreads, numThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new DaemonThreadFactory(handler, workerThreadNamePrefix));
    }

    private void init() throws InterruptedException {
        _execThread = new Thread(new Runnable() {

            @Override
            public void run() {
                LOG.info("Starting execution thread on pool thread " + Thread.currentThread().getName());
                while (!Thread.interrupted()) {
                    try {
                        RunnableWrapper rw = _execQ.take();
                        _internalPool.execute(rw);
                        LOG.info("RunnableWrapper " + rw.getName() + " resubmitted for execution.");
                    } catch (InterruptedException ie) {
                        LOG.info("Interrupted Exception in Exec Thread...exiting execThread.");
                    }
                }
                LOG.warn("Stopping execution thread on pool thread " + Thread.currentThread().getName());
            }

        });
        _execThread.setName("STP Exec Thread");
        _execThread.start();
    }

    public void join() throws InterruptedException {
        _internalPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public void execute(final Runnable r) {
        if (null == _execThread) {
            try {
                init();
            } catch (InterruptedException e) {
                throw new RuntimeException("Unable to init exec thread", e); //NOPMD
            }
        }
        if (r instanceof RunnableWrapper) {
            executeInternal((RunnableWrapper) r);
        } else {
            executeInternal(wrapRunnable(r));
        }
    }

    private void executeInternal(final RunnableWrapper rw) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Executing wrapped runnable: " + rw.getName());
        }
        _internalPool.execute(rw);
    }

    private RunnableWrapper wrapRunnable(final Runnable r) {
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
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return _internalPool.awaitTermination(timeout, unit);
    }

    // Unimplemented ExecutorService Methods
    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        throw new NotImplementedException();
    }

    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        throw new NotImplementedException();
    }

    @Override
    public Future<?> submit(final Runnable task) {
        throw new NotImplementedException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new NotImplementedException();
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException {
        throw new NotImplementedException();
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new NotImplementedException();
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new NotImplementedException();
    }
}
