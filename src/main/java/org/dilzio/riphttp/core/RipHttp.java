package org.dilzio.riphttp.core;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dilzio.riphttp.util.ApplicationParams;
import org.dilzio.riphttp.util.DaemonThreadFactory;
import org.dilzio.riphttp.util.ParamEnum;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;

/**
 * Fast HttpServer
 * @author dilzio
 *
 */
public class RipHttp {
	
	private static final Logger LOG = LogManager.getFormatterLogger(RipHttp.class.getName());
	private final ExecutorService _threadPool;
	private final ListenerDaemon _listenerThread;
	private final HttpWorker[] _httpWorkers;
	private final RingBuffer<HttpConnectionEvent> _ringBuffer;
	private final WorkerPool<HttpConnectionEvent> _workerPool;
	private final CyclicBarrier _shutdownLatch = new CyclicBarrier(2);
	private final CyclicBarrier _startUpBarrier;
	private final ApplicationParams _params;
	
	/**
	 * Will initialize with all default parameter values
	 * @see ParamEnum  
	 */
	public RipHttp(){
		this(new ApplicationParams());
	}
	public RipHttp(ApplicationParams params){
		_params = params;
		int numWorkers = params.getIntParam(ParamEnum.WORKER_COUNT);
		int port = params.getIntParam(ParamEnum.LISTEN_PORT); 
		int bufferSize = params.getIntParam(ParamEnum.RING_BUFFER_SIZE); 
		
		int numThreads = numWorkers + 1;
		_threadPool = Executors.newFixedThreadPool(numThreads, new DaemonThreadFactory());
		_httpWorkers = new HttpWorker[numWorkers];
		_startUpBarrier = new CyclicBarrier(numThreads);

		UriHttpRequestHandlerMapper uriHandlerMap = new URIRegistryFactory().getURIRegistry();

		for (int i = 0; i < numWorkers; i++){
			_httpWorkers[i] = new HttpWorker("Worker-" + i, uriHandlerMap, _startUpBarrier);
		}
		
		_ringBuffer = RingBuffer.createSingleProducer(HttpConnectionEvent.EVENT_FACTORY, bufferSize, new BlockingWaitStrategy());
		_workerPool = new WorkerPool<HttpConnectionEvent>(_ringBuffer, _ringBuffer.newBarrier(), new FatalExceptionHandler(), _httpWorkers);
		_ringBuffer.addGatingSequences(_workerPool.getWorkerSequences());
		
		_listenerThread = new ListenerDaemon(port, _ringBuffer);
	}
	

	public void start(){
		LOG.info("Starting riphttp");
		_workerPool.start(_threadPool);
		
		try {
			_startUpBarrier.await(_params.getIntParam(ParamEnum.HANDLER_AWAIT_MLLIS), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw new RuntimeException("Timed out waiting for Workers to start.", e);
		}
	
		_threadPool.submit(_listenerThread);
		try {
			_shutdownLatch.await();
		} catch (Exception e) {
			LOG.fatal("Unable to start server. Exiting.");
			System.exit(-1);
		}
		LOG.fatal("Exiting.");
	}

	public void stop(){
		try {
			_shutdownLatch.await();
  		    LOG.info("Shutting down riphttp");
		    _workerPool.drainAndHalt();
		} catch (Exception e) {
			LOG.error("Unable to shut down server. Exiting.");
			e.printStackTrace();
		}
	}
}
