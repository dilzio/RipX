package org.dilzio.ripx;

import com.lmax.disruptor.RingBuffer;

import java.util.concurrent.CountDownLatch;

/**
 * User: Matt C.
 * Date: 3/12/14
 * <p/>
 * Factory for creation of AbstractRipXWorker implementations
 */
public interface RipXWorkerFactory<T extends AbstractRipXWorker, E> {
    T[] getWorkerArray(int numWorkers, RingBuffer<E> ringBuffer);
    void injectStartupBarrier(final CountDownLatch startupLatch);
}
