package org.dilzio.ripx;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import org.dilzio.appparam.ApplicationParams;
import org.dilzio.appparam.ApplicationParamsFactory;
import org.dilzio.riphttp.disruptor.ext.WorkerPool;
import org.dilzio.riphttp.stp.SupervisoryThreadPoolFactory;
import org.dilzio.riphttp.util.PassthruExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: Matt C.
 * Date: 3/12/14
 *
 * Framework for building Reactive servers on top of Disruptor.
 *
 * On construction, caller provides the following components:
 *
 * - application parameters
 * - factory for event structs to be placed in the ring buffer
 * - worker factory which returns an array of worker runnables
 * - producer factory which return an array of producer runnables
 *
 * The start() and init() methods will do the wiring, initialize a thread pool, and submit the producer and consumer runnables
 * for execution.
 */
public class RipX<E, T extends AbstractRipXWorker<E>> {

	private static final Logger LOG = LoggerFactory.getLogger(RipX.class.getName());
    private RipXWorkerFactory _workerFactory;
    private EventFactory<E> _eventFactory;
    private ExecutorService _threadPool;
	private WorkerPool<E> _workerPool;
	private CountDownLatch _startUpLatch;
	private final List<RipXProducer> _listenerList = new LinkedList<>();
	private ApplicationParams<RipXProperties>  _params;
	private final AtomicBoolean _startedFlag = new AtomicBoolean(false);
    private RipXProducerFactory _producerFactory;


    public RipX(final String configPath, final EventFactory<E> eventFactory, final RipXWorkerFactory<T, E> workerFactory, final RipXProducerFactory producerFactory) {
        ApplicationParams<RipXProperties> params =  ApplicationParamsFactory.getInstance().newParams(RipXProperties.values(), configPath, true);
        construct(params, eventFactory, workerFactory, producerFactory);
	}

    public RipX(final ApplicationParams<RipXProperties> params, final EventFactory<E> eventFactory, final RipXWorkerFactory<T, E> workerFactory, final RipXProducerFactory producerFactory) {
       construct(params, eventFactory, workerFactory,producerFactory);
    }

    private void construct(final ApplicationParams<RipXProperties> params, final EventFactory<E> eventFactory, final RipXWorkerFactory<T, E> workerFactory, final RipXProducerFactory producerFactory) {
        if (null == eventFactory) {
            throw new IllegalArgumentException("eventFactory cannot be null");
        }

        if (null == workerFactory) {
            throw new IllegalArgumentException("workerFactory cannot be null");
        }

        if (null == producerFactory) {
            throw new IllegalArgumentException("producerFactory cannot be null");
        }
        _params = params;
        _eventFactory = eventFactory;
        _workerFactory = workerFactory;
        _producerFactory = producerFactory;
    }
	private void init() {
        final int bufferSize = _params.getIntParam(RipXProperties.RING_BUFFER_SIZE);
        final String workerThreadNamePrefix = _params.getStringParam(RipXProperties.WORKER_THREAD_NAME_PREFIX);

        final int numWorkers = _params.getIntParam(RipXProperties.WORKER_COUNT);
        final int numConfiguredListeners = _params.getIntParam(RipXProperties.NUM_LISTENERS);
        final int threadPoolSize= _params.getIntParam(RipXProperties.THREAD_POOL_SIZE);
		_threadPool = SupervisoryThreadPoolFactory.getInstance().newOneForOneSupervisoryThreadPool(_params.getIntParam(RipXProperties.ONE_FOR_ONE_MAX_RESTARTS),
                                                                                                    _params.getIntParam(RipXProperties.ONE_FOR_ONE_RESTART_WINDOW_MILLIS),
																								   threadPoolSize, workerThreadNamePrefix);

        int barrierCount = numWorkers;
        _startUpLatch = new CountDownLatch(barrierCount);
		RingBuffer<E> ringBuffer = createRingBuffer(bufferSize, numConfiguredListeners);
        AbstractRipXWorker[] workerArray  = _workerFactory.getWorkerArray(numWorkers, ringBuffer);

        logWorkerArray(workerArray);
 		_workerPool = new WorkerPool<>(ringBuffer, ringBuffer.newBarrier(), new PassthruExceptionHandler(), workerArray);


        _listenerList.addAll(_producerFactory.getProducerCollection(ringBuffer, numConfiguredListeners)); //NOPMD
	}

    private void logWorkerArray(final AbstractRipXWorker[] workerArray) {
        StringBuilder buf = new StringBuilder(100);
        buf.append(workerArray.length);
        buf.append(" worker instances configured \n");
        for (AbstractRipXWorker worker : workerArray){
            buf.append("Added: " + worker.getName() + "\n");
        }
        LOG.info(buf.toString());
    }

    private RingBuffer<E> createRingBuffer(final int bufferSize, final int numConfiguredListeners) {
		RingBuffer<E> ringBuffer;
		if (numConfiguredListeners < 1){
			throw new IllegalArgumentException("NUM_LISTENERS property must have min val of 1");
		}else if (numConfiguredListeners == 1){
			ringBuffer= RingBuffer.createSingleProducer(_eventFactory, bufferSize, new BlockingWaitStrategy());
		}else{
			ringBuffer = RingBuffer.createMultiProducer(_eventFactory, bufferSize, new BlockingWaitStrategy());
		}
		return ringBuffer;
	}

	public void start() {
		LOG.info("Starting RipX with configured parameters (all other params will be hardcoded default):\n" + _params.toString());

		if (_startedFlag.get()) {
			throw new IllegalStateException("RipHttp server already started.");
		}

		init();
		_workerPool.start(_threadPool);

		//wait for worker threads to all be ready
		try {
            _startUpLatch.await(_params.getIntParam(RipXProperties.STARTUP_TIMEOUT_MS), TimeUnit.MILLISECONDS);
		} catch (Exception e){
			throw new RuntimeException("Timed out waiting for Workers to start.", e); //NOPMD
		}

		//start the listener threads and give x millis to initialize before returning control
		LOG.info("Starting " + _listenerList.size() + " listeners.");
		for (Runnable l: _listenerList){
			_threadPool.execute(l);
		}
		
		try {
            Thread.sleep(_params.getIntParam(RipXProperties.LISTENER_THREAD_SLEEP_ON_STARTUP_MILLIS));
		} catch (InterruptedException e) {
			throw new RuntimeException("Error while sleeping listener thread on startup.", e); //NOPMD
		}

        _startedFlag.set(true);
	}

	public void stop() {
		try {
			LOG.info("Shutting down RipX");
			for (RipXProducer l: _listenerList){
				l.stop();
			}
			_workerPool.drainAndHalt();
			_threadPool.shutdown();
			_startedFlag.set(false);
            _threadPool.awaitTermination(_params.getIntParam(RipXProperties.POOL_SHUTDOWN_AWAIT_MILLIS), TimeUnit.MILLISECONDS);
            _listenerList.clear();
		} catch (Exception e) {
			LOG.error("Unable to shut down server. Exiting.", e);
		}
	}

	public void join() throws InterruptedException {
		_threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

    public boolean isRunning() {
        return _startedFlag.get();
    }
}
