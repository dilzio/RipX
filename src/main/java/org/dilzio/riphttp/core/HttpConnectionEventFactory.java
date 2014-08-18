package org.dilzio.riphttp.core;

import com.lmax.disruptor.EventFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * User: Matt C.
 * Date: 3/13/14
 *
 * Factory class for connection events.  The Disruptor framework calls this on initialization 1x per slot
 * in the ring buffer.
 */
public class HttpConnectionEventFactory implements EventFactory<HttpConnectionEvent> {
    private final AtomicLong _idGenerator = new AtomicLong();

    @Override
    public HttpConnectionEvent newInstance() {
        return new HttpConnectionEvent( _idGenerator.getAndIncrement());
    }
}
