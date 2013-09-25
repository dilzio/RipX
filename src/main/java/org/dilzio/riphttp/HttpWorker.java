package org.dilzio.riphttp;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandler;
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
	
	public HttpWorker(final String name){
		_name = name;
		_httpProc = HttpProcessorBuilder.create().add(new ResponseDate())
												 .add(new ResponseServer("Test/1.1"))
												 .add(new ResponseContent())
												 .add(new ResponseConnControl()).build();
		UriHttpRequestHandlerMapper registry = new UriHttpRequestHandlerMapper();
		registry.register("*", new HttpRequestHandler(){

			@Override
			public void handle(HttpRequest request, HttpResponse response,
					HttpContext context) throws HttpException, IOException {
				response.setStatusCode(HttpStatus.SC_OK);
				response.setEntity(new StringEntity("200 OK"));
			}
		});
		
		_httpService = new HttpService(_httpProc, registry);
	}
	@Override
	public void setEventReleaser(EventReleaser eventReleaser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(final HttpConnectionEvent event) throws Exception {
		
		LOG.info("Handler %s on Event number: %s", _name, event.getId());
		HttpServerConnection httpCon = event.get_httpConn();
		if (null == httpCon){
			LOG.error("Event seq: %s received with null http connection object. Throwing Exception.", event.getId());
			throw new RuntimeException("Null http connection on event.");
		}
		try{
			_httpService.handleRequest(httpCon, new BasicHttpContext(null));
			LOG.info("Handler %s sucessfully processed event: %s", _name, event.getId());
		}
		finally{
			//httpCon.shutdown();
		}
	}

}
