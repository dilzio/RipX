package org.dilzio.riphttp;

import java.io.IOException;
import java.net.SocketException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.EventReleaseAware;
import com.lmax.disruptor.EventReleaser;
import com.lmax.disruptor.WorkHandler;

public class HttpWorker implements WorkHandler<HttpConnectionEvent>, EventReleaseAware {

	private static final Logger LOG = LogManager.getFormatterLogger(HttpWorker.class.getName());
	private final String _name;
	private final HttpProcessor _httpProc;
	private final HttpService _httpService;
	
	public HttpWorker(final String name, final UriHttpRequestHandlerMapper registry){
		_name = name;
		_httpProc = HttpProcessorBuilder.create().add(new ResponseDate())
												 .add(new ResponseServer("Test/1.1"))
												 .add(new ResponseContent())
												 .add(new ResponseConnControl()).build();
		_httpService = new HttpService(_httpProc, registry);
	}
	@Override
	public void setEventReleaser(EventReleaser eventReleaser) {}

	@Override
	public void onEvent(final HttpConnectionEvent event) throws Exception {
		
		LOG.info("Handler %s on Event number: %s", _name, event.getId());
		HttpServerConnection httpCon = event.get_httpConn();
		if (null == httpCon){
			LOG.error("Event seq: %s received with null http connection object. Throwing Exception.", event.getId());
			throw new RuntimeException("Null http connection on event.");
		}
		try{
			  LOG.info("Handler %s received event: %s", _name, event.getId());
			  _httpService.handleRequest(httpCon, new BasicHttpContext(null));
			  LOG.info("Handler %s sucessfully processed event: %s", _name, event.getId());
		} catch (ConnectionClosedException ce){
			LOG.warn("ConnectionClosed Exception event %s", event.getId());
		} catch (SocketException se){
			LOG.warn("Socket Exception event %s", event.getId());
		}finally{
			try{
				httpCon.shutdown();
			}catch(IOException ignore) {}
		}
	}

}
