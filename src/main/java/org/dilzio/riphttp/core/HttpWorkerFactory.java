package org.dilzio.riphttp.core;

import com.lmax.disruptor.RingBuffer;
import org.apache.http.protocol.HttpRequestHandlerMapper;
import org.dilzio.appparam.ApplicationParams;
import org.dilzio.riphttp.util.BasicTimeService;
import org.dilzio.ripx.RipXWorkerFactory;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * User: Matt C.
 * Date: 3/12/14
 */
public class HttpWorkerFactory implements RipXWorkerFactory<HttpWorker, HttpConnectionEvent> {
    private final ApplicationParams<RipHttpProperties>  _params;
    private final Route[] _routes;
    private CountDownLatch _startupLatch;

    public HttpWorkerFactory(final ApplicationParams<RipHttpProperties> params, final Route[] routes) {
        _params = params;
        _routes = Arrays.copyOf(routes, routes.length);
    }

    @Override
    public HttpWorker[] getWorkerArray(final int numWorkers, final RingBuffer<HttpConnectionEvent> ringBuffer) {
        HttpWorker[] httpWorkers = new HttpWorker[numWorkers];
        HttpRequestHandlerMapper handlerMap = buildMapper();

        for (int i = 0; i < numWorkers; i++){
            httpWorkers[i] = new HttpWorker(ringBuffer, _params.getStringParam(RipHttpProperties.SERVER_NAME), _params.getStringParam(RipHttpProperties.SERVER_VERSION), "Worker-" + i, handlerMap, _startupLatch, new BasicTimeService()); //NOPMD
        }

        return httpWorkers;
    }

    @Override
    public void injectStartupBarrier(final CountDownLatch startupLatch) {
       _startupLatch = startupLatch;
    }

    private HttpRequestHandlerMapper buildMapper() {
        RouteHttpRequestHandlerMapper mapper = new RouteHttpRequestHandlerMapper(new RoutePatternMatcher());
        for (Route r : _routes) {
            mapper.register(r);
        }

        return mapper;
    }
}
