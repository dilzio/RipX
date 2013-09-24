package org.dilzio.riphttp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.EventReleaseAware;
import com.lmax.disruptor.EventReleaser;
import com.lmax.disruptor.WorkHandler;

public class HttpWorker implements WorkHandler<HttpConnectionEvent>, EventReleaseAware {

	private static final Logger LOG = LogManager.getFormatterLogger(HttpWorker.class.getName());
	@Override
	public void setEventReleaser(EventReleaser eventReleaser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(HttpConnectionEvent event) throws Exception {
		LOG.error("Got event %s", event.get_tempVal());
		
	}

}
