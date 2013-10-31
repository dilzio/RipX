package org.dilzio.riphttp.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.EventHandler;

/**
 * Worker which follows all HTTPWorkers on the Ring Buffer. Captures and Logs Metrics for completed events
 * @author dilzio
 *
 */
public class MetricsWorker implements EventHandler<HttpConnectionEvent> {

	private static final Logger LOG = LogManager.getFormatterLogger(MetricsWorker.class.getName());

	@Override
	public void onEvent(HttpConnectionEvent event, long sequence, boolean endOfBatch) throws Exception {
		long id = event.getId();
		long writeTs = event.getWriteTimestampMillis();
		long readBeginTs = event.getReadBeginTimestampMillis();
		long readEndTs = event.getReadEndTimestampMillis();
		long readTime = readEndTs - readBeginTs;
		long totalTime = readEndTs - writeTs;
		long writeToReadTime = readBeginTs - writeTs;
		String workerName = event.getWorkerName();
		LOG.info("EventTimingLog [sequence=%s, eventid=%s, worker=%s, beg_write_to_beg_read_ms=%s, tot_read_ms=%s, tot_time=%s", sequence, id, workerName, writeToReadTime, readTime, totalTime);
		//Clean up event...should probably move this to a dedicated finalization worker responsible for cleaning up used events
		event.reset();
	}

}
