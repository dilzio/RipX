package org.dilzio.riphttp;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;

public class RipHttp {
	
	private static final Logger LOG = LogManager.getFormatterLogger(RipHttp.class.getName());
	private final ExecutorService _threadPool;
	private final ListenerDaemon _listenerThread;
	private final HttpWorker[] _httpWorkers;
	private final RingBuffer<HttpConnectionEvent> _ringBuffer;
	private final WorkerPool<HttpConnectionEvent> _workerPool;
	private final CyclicBarrier _shutdownLatch = new CyclicBarrier(2);
	private final CyclicBarrier _startUpBarrier;

	public RipHttp(){
		//// TODO - PASS IN ////
		int numWorkers = 6;
		int port = 8081;
		int bufferSize = 1024 * 8;
		//////////////////////////
		int numThreads = numWorkers + 1;
		_threadPool = Executors.newFixedThreadPool(numThreads);
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
			_startUpBarrier.await(1L, TimeUnit.SECONDS);
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
