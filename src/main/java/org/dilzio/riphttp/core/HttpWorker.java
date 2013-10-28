package org.dilzio.riphttp.core;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandlerMapper;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dilzio.riphttp.util.ITimeService;

import com.lmax.disruptor.EventReleaseAware;
import com.lmax.disruptor.EventReleaser;
import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.WorkHandler;

public class HttpWorker implements WorkHandler<HttpConnectionEvent>, LifecycleAware, EventReleaseAware {

	private static final Logger LOG = LogManager.getFormatterLogger(HttpWorker.class.getName());
	private final String _name;
	private final HttpService _httpService;
	private final CyclicBarrier _startUpBarrier;
	private final ITimeService _timeService;
	private boolean _isInitialRun;

	public HttpWorker(final String serverName, final String serverVsn, final String name, final HttpRequestHandlerMapper registry, final CyclicBarrier startUpBarrier, final ITimeService timeService) {
		_name = name;
		HttpProcessor httpProc = HttpProcessorBuilder.create().add(new ResponseDate()).add(new ResponseServer(serverName + "/" + serverVsn)).add(new ResponseContent()).add(new ResponseConnControl()).build();
		_httpService = new HttpService(httpProc, registry);
		_startUpBarrier = startUpBarrier;
		_isInitialRun = true;
		_timeService = timeService;

	}

	@Override
	public void setEventReleaser(EventReleaser eventReleaser) {/* TODO */
	}

	@Override
	public void onEvent(final HttpConnectionEvent event) throws Exception {
		event.setReadBeginTimestampMillis(_timeService.microTime());
		LOG.trace("Handler %s on Event number: %s", _name, event.getId());
		HttpServerConnection httpCon = event.get_httpConn();
		if (null == httpCon) {
			LOG.error("Event seq: %s received with null http connection object. Throwing Exception.", event.getId());
			throw new RuntimeException("Null http connection on event.");
		}
		try {
			LOG.trace("Handler %s received event: %s", _name, event.getId());
			_httpService.handleRequest(httpCon, new BasicHttpContext(null));
			LOG.trace("Handler %s sucessfully processed event: %s", _name, event.getId());
		} catch (ConnectionClosedException ce) {
			LOG.warn("ConnectionClosed Exception event %s", event.getId());
		} catch (SocketException se) {
			LOG.warn("Socket Exception event %s", event.getId());
		} finally {
			try {
				httpCon.shutdown();
				event.set_httpConn(null); // very important, else mem will be
											// eaten by used events
				event.setReadEndTimestampMillis(_timeService.microTime());
			} catch (IOException ignore) {
				LOG.warn("threw IOException when attempting to shutdown httpcCon: %s", ignore.getMessage());
			}
		}
	}

	@Override
	public void onStart() {
		if (_isInitialRun){
			LOG.info("Worker %s awaiting...", _name);
			try {
				_startUpBarrier.await(5L, TimeUnit.SECONDS);
				_isInitialRun = false;
			} catch (Exception e) {
				LOG.info("Worker %s threw exception while waiting on barrier.", _name);
				throw new RuntimeException(e);
			}
		}
		
		LOG.info("Worker %s started on thread %s", _name, Thread.currentThread().getName());
	}

	@Override
	public void onShutdown() {
		LOG.info("Shutting down Worker %s.", _name);

	}

}
