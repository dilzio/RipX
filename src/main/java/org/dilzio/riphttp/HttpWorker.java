package org.dilzio.riphttp;

import org.apache.http.HttpServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.EventReleaseAware;
import com.lmax.disruptor.EventReleaser;
import com.lmax.disruptor.WorkHandler;

public class HttpWorker implements WorkHandler<HttpConnectionEvent>, EventReleaseAware {

	private static final Logger LOG = LogManager.getFormatterLogger(HttpWorker.class.getName());
	private final String _name;
	
	public HttpWorker(final String name){
		_name = name;
	}
	@Override
	public void setEventReleaser(EventReleaser eventReleaser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(final HttpConnectionEvent event) throws Exception {
		
		HttpServerConnection httpCon = event.get_httpConn();
		if (null == httpCon){
			LOG.error("Event seq: %s received with null http connection object. Throwing Exception.", event.getId());
			throw new RuntimeException("Null http connection on event.");
		}
		try{
			LOG.error("%s connection is open: %s Event %s", _name, httpCon.isOpen(), event.getId());
		}
		finally{
			httpCon.shutdown();
		}
	}

}
