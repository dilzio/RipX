package org.dilzio.riphttp.core;

import com.lmax.disruptor.RingBuffer;
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
import org.dilzio.riphttp.util.ITimeService;
import org.dilzio.ripx.AbstractRipXWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings("PMD")
public class HttpWorker extends AbstractRipXWorker<HttpConnectionEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpWorker.class.getName());
    private final HttpService _httpService;
    private final ITimeService _timeService;

    public HttpWorker(final RingBuffer<HttpConnectionEvent> ringBuffer, final String serverName, final String serverVsn, final String name, final HttpRequestHandlerMapper registry, final CountDownLatch startUpLatch, final ITimeService timeService) {
        super(ringBuffer, startUpLatch, name);
        HttpProcessor httpProc = HttpProcessorBuilder.create().add(new ResponseDate()).add(new ResponseServer(serverName + "/" + serverVsn)).add(new ResponseContent()).add(new ResponseConnControl()).build();
        _httpService = new HttpService(httpProc, registry);
        _timeService = timeService;

    }

    @Override
    public void onEvent(final HttpConnectionEvent event) throws Exception { //NOPMD
        event.setReadBeginTimestampMicros(_timeService.microTime());
        String name = getName();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Handler " + name + " on Event number: " + event.getId());
        }
        HttpServerConnection httpCon = event.getHttpConn();
        if (null == httpCon) {
            LOG.error("Event seq: " + event.getId() + " received with null http connection object.");
        }
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Handler " + name + " received event: " + event.getId());
            }
            _httpService.handleRequest(httpCon, new BasicHttpContext(null));  //TODO: something real with context
            if (LOG.isTraceEnabled()) {
                LOG.trace("Handler " + name + " successfully processed event: " + event.getId());
            }
        } catch (ConnectionClosedException ce) {

        } catch (SocketException se) {
            LOG.warn("Socket Exception event " + event.getId());
        } catch (Exception e) {
            LOG.warn("Exception event " + event.getId() + "|exception Type: " + e.getClass() + "|exceptionMessage: " + e.getMessage());
        } finally {
            try {
                if (null != httpCon) {
                    httpCon.shutdown();
                }
                event.setHttpConn(null); // very important, else mem will be eaten by used events
                event.setWorkerName(name);
                event.setReadEndTimestampMicros(_timeService.microTime());
            } catch (IOException ignore) {
                LOG.warn("threw IOException when attempting to shutdown httpCon: " + ignore.getMessage());
            }
        }
    }
}
