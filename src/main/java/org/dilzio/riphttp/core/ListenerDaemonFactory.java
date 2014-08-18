package org.dilzio.riphttp.core;

import com.lmax.disruptor.RingBuffer;
import org.apache.http.util.Args;
import org.dilzio.appparam.ApplicationParams;
import org.dilzio.riphttp.util.BasicServerSocketFactory;
import org.dilzio.riphttp.util.BasicTimeService;
import org.dilzio.riphttp.util.SSLServerSocketFactory;
import org.dilzio.ripx.RipXProducer;
import org.dilzio.ripx.RipXProducerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Matt C.
 * Date: 3/12/14
 * <p/>
 * <p/>
 * Factory class to build Listener Daemons to listen for incoming HTTP requests
 */
public class ListenerDaemonFactory implements RipXProducerFactory<HttpConnectionEvent> {


    private final ApplicationParams<RipHttpProperties> _params;

    public ListenerDaemonFactory(final ApplicationParams<RipHttpProperties> params) {
        _params = params;
    }

    @Override
    public Collection<? extends RipXProducer> getProducerCollection(final RingBuffer<HttpConnectionEvent> ringBuffer, final int numProducers) {

        List<RipXProducer> producers = new LinkedList<>();

        try {
            if (_params.getBoolParam(RipHttpProperties.USE_HTTP)) {
                addHttpListeners(producers, ringBuffer, numProducers);
            }

            if (_params.getBoolParam(RipHttpProperties.USE_HTTPS)) {
                addHttpsListeners(producers, ringBuffer, numProducers);
            }
        } catch (IOException e) {
            throw new RuntimeException(e); //NOPMD
        }

        return producers;
    }

    private void addHttpListeners(final List<RipXProducer> producers, final RingBuffer<HttpConnectionEvent> ringBuffer, final int numProducers) throws IOException {
        final int httpPort = _params.getIntParam(RipHttpProperties.HTTP_LISTEN_PORT);
        ServerSocket listenerSocket = new BasicServerSocketFactory().getServerSocket(httpPort);
        for (int i = 0; i < numProducers; i++) {
            producers.add(new ListenerDaemon("HttpListener-" + i, httpPort, ringBuffer, listenerSocket, _params.getBoolParam(RipHttpProperties.POISON_PILL), new BasicTimeService(), _params.getIntParam(RipHttpProperties.POISON_PILL_KILL_EVERY))); //NOPMD
        }
    }

    private void addHttpsListeners(final List<RipXProducer> producers, final RingBuffer<HttpConnectionEvent> ringBuffer, final int numProducers) throws IOException {
        final int httpsPort = _params.getIntParam(RipHttpProperties.HTTPS_LISTEN_PORT);
        String keystore = _params.getStringParam(RipHttpProperties.SSL_KEYSTORE);
        String keystorePassword = _params.getStringParam(RipHttpProperties.SSL_KEYSTORE_PASSWORD);
        Args.notEmpty(keystore, "keystore param was empty.");
        Args.notEmpty(keystorePassword, "keystore password param was empty.");
        ServerSocket listenerSocket = new SSLServerSocketFactory(keystore, keystorePassword).getServerSocket(httpsPort);
        for (int i = 0; i < numProducers; i++) {
            producers.add(new ListenerDaemon("HttpsListener-" + i, httpsPort, ringBuffer, listenerSocket, _params.getBoolParam(RipHttpProperties.POISON_PILL), new BasicTimeService(), _params.getIntParam(RipHttpProperties.POISON_PILL_KILL_EVERY))); //NOPMD
        }
    }

}
