package org.dilzio.ripx;

import com.lmax.disruptor.RingBuffer;

import java.util.Collection;

/**
 * User: Matt C.
 * Date: 3/12/14
 * <p/>
 * Implementation return a collection of producer threads
 */
public interface RipXProducerFactory<E> {
    Collection<? extends RipXProducer> getProducerCollection(final RingBuffer<E> ringBuffer, int numProducers);
}
