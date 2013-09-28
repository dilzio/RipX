package org.dilzio.riphttp;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.FatalExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;

public class RipHttp {
	
	private static final Logger LOG = LogManager.getFormatterLogger(RipHttp.class.getName());
	private final ExecutorService _threadPool;
	private final ListenerDaemon _listenerThread;
	private final HttpWorker[] _httpWorkers;
	private final RingBuffer<HttpConnectionEvent> _ringBuffer;
	private final WorkerPool<HttpConnectionEvent> _workerPool;

	private final CyclicBarrier _shutdownLatch = new CyclicBarrier(2);

	public RipHttp(){
		//// TODO - PASS IN ////
		int numWorkers = 10;
		int port = 8081;
		int bufferSize = 1024 * 8;
		//////////////////////////
		_threadPool = Executors.newFixedThreadPool(numWorkers + 1);
		_httpWorkers = new HttpWorker[numWorkers];
		UriHttpRequestHandlerMapper uriHandlerMap = new URIRegistryFactory().getURIRegistry();
		for (int i = 0; i < numWorkers; i++){
			_httpWorkers[i] = new HttpWorker("Worker-" + i, uriHandlerMap);
		}
		
		_ringBuffer = RingBuffer.createSingleProducer(HttpConnectionEvent.EVENT_FACTORY, bufferSize, new YieldingWaitStrategy());
		_workerPool = new WorkerPool<HttpConnectionEvent>(_ringBuffer, _ringBuffer.newBarrier(), new FatalExceptionHandler(), _httpWorkers);
		_ringBuffer.addGatingSequences(_workerPool.getWorkerSequences());
		
		_listenerThread = new ListenerDaemon(port, _ringBuffer);
	}
	
	public void start(){
		LOG.info("Starting riphttp");
		_workerPool.start(_threadPool);
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
		} catch (Exception e) {
			LOG.error("Unable to start server. Exiting.");
			e.printStackTrace();
		}
	}
}
