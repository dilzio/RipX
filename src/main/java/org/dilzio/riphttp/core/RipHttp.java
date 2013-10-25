package org.dilzio.riphttp.core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.protocol.HttpRequestHandlerMapper;
import org.apache.http.util.Args;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dilzio.riphttp.disruptor.ext.WorkerPool;
import org.dilzio.riphttp.util.BasicServerSocketFactory;
import org.dilzio.riphttp.util.ParamEnum;
import org.dilzio.riphttp.util.PassthruExceptionHandler;
import org.dilzio.riphttp.util.SSLServerSocketFactory;
import org.dilzio.riphttp.util.ServerSocketFactory;
import org.dilzio.ripphttp.appparam.ApplicationParams;
import org.dilzio.ripphttp.util.stp.SupervisoryThreadPoolFactory;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;

/**
 * Fast HttpServer
 * 
 * @author dilzio
 * 
 */
public class RipHttp {

	private static final Logger LOG = LogManager.getFormatterLogger(RipHttp.class.getName());
	private ExecutorService _threadPool;
	private ListenerDaemon _listenerThread;
	private WorkerPool<HttpConnectionEvent> _workerPool;
	private CyclicBarrier _startUpBarrier;
	private final ApplicationParams _params;
	private final Queue<Route> _routeList = new LinkedList<Route>();
	private final AtomicBoolean _startedFlag = new AtomicBoolean(false);

	/**
	 * Will initialize with all default parameter values
	 * 
	 * @see ParamEnum
	 */
	public RipHttp() {
		this(new ApplicationParams());
	}

	public RipHttp(final ApplicationParams appParams) {
		if (null == appParams) {
			throw new IllegalArgumentException("appParams cannot be null");
		}
		_params = appParams;
	}

	private void init() {
		int numWorkers = _params.getIntParam(ParamEnum.WORKER_COUNT);
		int port = _params.getIntParam(ParamEnum.LISTEN_PORT);
		int bufferSize = _params.getIntParam(ParamEnum.RING_BUFFER_SIZE);
		int threadPoolSize = _params.getIntParam(ParamEnum.THREAD_POOL_SIZE);
		
		
		HttpWorker[] httpWorkers = new HttpWorker[numWorkers];
		_threadPool = SupervisoryThreadPoolFactory.getInstance().newOneForOneSupervisoryThreadPool(_params.getIntParam(ParamEnum.ONE_FOR_ONE_MAX_RESTARTS),
																								   _params.getIntParam(ParamEnum.ONE_FOR_ONE_RESTART_WINDOW_MILLIS),
																								   threadPoolSize);
		
		_startUpBarrier = new CyclicBarrier(numWorkers + 1); //the +1 is for the listener thread

		for (int i = 0; i < numWorkers; i++) {
			HttpRequestHandlerMapper handlerMap = buildMapper(); // each worker
																	// gets its
																	// own
																	// thread-local
																	// mapper
			httpWorkers[i] = new HttpWorker(_params.getStringParam(ParamEnum.SERVER_NAME), _params.getStringParam(ParamEnum.SERVER_VERSION), "Worker-" + i, handlerMap, _startUpBarrier);
		}

		RingBuffer<HttpConnectionEvent> ringBuffer = RingBuffer.createSingleProducer(HttpConnectionEvent.EVENT_FACTORY, bufferSize, new BlockingWaitStrategy());
		_workerPool = new WorkerPool<HttpConnectionEvent>(ringBuffer, ringBuffer.newBarrier(), new PassthruExceptionHandler(), httpWorkers);
		ringBuffer.addGatingSequences(_workerPool.getWorkerSequences());

		_listenerThread = new ListenerDaemon(port, ringBuffer, getSocketFactory(_params), _params.getBoolParam(ParamEnum.POISON_PILL));
	}

	private ServerSocketFactory getSocketFactory(final ApplicationParams params) {
		if (params.getBoolParam(ParamEnum.USE_SSL)) {
			String keystore = params.getStringParam(ParamEnum.SSL_KEYSTORE);
			String keystorePassword = params.getStringParam(ParamEnum.SSL_KEYSTORE_PASSWORD);
			Args.notEmpty(keystore, "keystore param was empty.");
			Args.notEmpty(keystorePassword, "keystore password param was empty.");
			return new SSLServerSocketFactory(keystore, keystorePassword);
		} else {
			return new BasicServerSocketFactory();
		}
	}

	private HttpRequestHandlerMapper buildMapper() {
		RouteHttpRequestHandlerMapper mapper = new RouteHttpRequestHandlerMapper(new RoutePatternMatcher());
		for (Route r : _routeList) {
			mapper.register(r);
		}

		return mapper;
	}

	public void start() {
		LOG.info("Starting Riphttp with configured parameters (all other params will be hardcoded default):\n%s", _params.toString());
		if (_routeList.isEmpty()) {
			throw new IllegalStateException("At least one Handler must be configured.");
		}

		if (_startedFlag.get()) {
			throw new IllegalStateException("RipHttp server already started.");
		}

		init();
		_workerPool.start(_threadPool);

		//wait for worker threads to all be ready
		try {
			_startUpBarrier.await(_params.getIntParam(ParamEnum.HANDLER_AWAIT_MILLIS), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw new RuntimeException("Timed out waiting for Workers to start.", e);
		}

		//start the listener thread and give x millis to initialize before returning control
		_threadPool.execute(_listenerThread);
		try {
			Thread.sleep(_params.getIntParam(ParamEnum.LISTENER_THREAD_SLEEP_ON_STARTUP_MILLIS));
		} catch (InterruptedException e) {
			throw new RuntimeException("Error while sleeping listener thread on startup.", e);
		}
	}

	public void stop() {
		try {
			LOG.info("Shutting down riphttp");
			_listenerThread.stop();
			_workerPool.drainAndHalt();
			_threadPool.shutdown();
			_startedFlag.set(false);
			_threadPool.awaitTermination(_params.getIntParam(ParamEnum.POOL_SHUTDOWN_AWAIT_MILLIS), TimeUnit.MILLISECONDS);

		} catch (Exception e) {
			LOG.error("Unable to shut down server. Exiting.");
			e.printStackTrace();
		}
	}

	public void join() throws InterruptedException {
		_threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

	public void addHandlers(final Route... routes) {
		if (_startedFlag.get()){
			throw new IllegalStateException("Cannot add handlers while server is started.");
		}

		if (null == routes || routes.length == 0) {
			throw new IllegalArgumentException("tried to add null or empty routes.");
		}

		_routeList.clear();

		for (Route rt : routes) {
			_routeList.add(rt);
		}
	}
}
