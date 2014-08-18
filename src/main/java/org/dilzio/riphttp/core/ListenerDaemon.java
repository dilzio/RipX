package org.dilzio.riphttp.core;

import com.lmax.disruptor.RingBuffer;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.dilzio.riphttp.util.ITimeService;
import org.dilzio.ripx.RipXProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ListenerDaemon implements RipXProducer {
    private static final Logger LOG = LoggerFactory.getLogger(ListenerDaemon.class.getName());
    private final HttpConnectionFactory<DefaultBHttpServerConnection> _connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
    private final int _port;
    private final RingBuffer<HttpConnectionEvent> _ringBuffer;
    private final AtomicBoolean _isShutdown = new AtomicBoolean(false);
    private final ITimeService _timeService;
    private final boolean _poison;
    private final int _poisonKillEvery;
    private final String _name;
    private final ServerSocket _listenerSocket;
    private Thread _runThread;

    public ListenerDaemon(final String name, final int port, final RingBuffer<HttpConnectionEvent> ringBuffer, final ServerSocket listenerSocket, final boolean poison, final ITimeService timeService, final int poisonKillEvery) {
        _port = port;
        _ringBuffer = ringBuffer;
        _poison = poison;
        _poisonKillEvery = poisonKillEvery;
        _timeService = timeService;
        _name = name;
        _listenerSocket = listenerSocket;
    }

    @Override
    public void run() {
        _runThread = Thread.currentThread();

        LOG.info(_name + " listening for incoming connections on port " + _port);
        while (!Thread.interrupted()) {
            long sequence = Long.MIN_VALUE;
            try {
                Socket connectionSocket = _listenerSocket.accept();
                final long writeStart = _timeService.microTime();
                HttpServerConnection httpConnection = _connFactory.createConnection(connectionSocket);
                sequence = _ringBuffer.next();
                if (_poison && sequence > 0 && (sequence % _poisonKillEvery) == 0) {
                    new RuntimeException("Listener Poison Pill Test RTE"); //NOPMD
                }
                HttpConnectionEvent event = _ringBuffer.get(sequence);
                event.setHttpConn(httpConnection);
                event.setBeginWriteTimestampMicros(writeStart);
                event.setEndWriteTimestampMicros(_timeService.microTime());
                if (LOG.isTraceEnabled()) {
                    LOG.trace(_name + " published event " + event.getId());
                }
            } catch (IOException e) {
                if (_isShutdown.get()) {
                    return;
                }
                LOG.error("Unable to accept connection: " + e.getMessage());
            } finally {
                // always publish event if ringbuffer sequence was incremented with _ringBuffer.next()
                if (sequence != Long.MIN_VALUE) {
                    _ringBuffer.publish(sequence);
                }
            }
        }
        LOG.info("Listener thread " + _runThread.getName() + " exiting");
    }

    @Override
    public void stop() {
        _isShutdown.set(true);
        _runThread.interrupt();
        try {
            if (null != _listenerSocket) {
                _listenerSocket.close();
            }
        } catch (IOException e) {
            LOG.warn("IOException thrown on listenerSocket close");
        }
    }

}
