package org.dilzio.ripx;

/**
 * User: Matt C.
 * Date: 3/12/14
 *
 * Base class for RipX Workers.
 */

import com.lmax.disruptor.EventReleaseAware;
import com.lmax.disruptor.EventReleaser;
import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public abstract class AbstractRipXWorker<E> implements WorkHandler<E>, LifecycleAware, EventReleaseAware {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRipXWorker.class.getName());


    private final RingBuffer<E> _ringBuffer;
    private final CountDownLatch _startupLatch;
    private final String _name;
    private EventReleaser _eventReleaser;
    private boolean _isInitialRun;

    protected AbstractRipXWorker(final RingBuffer<E> ringBuffer, final CountDownLatch startupLatch, final String name) {
        _ringBuffer = ringBuffer;
        _startupLatch = startupLatch;
        _name = name;
    }

    @Override
    public void onStart() {
        if (_isInitialRun) {
            try {
                _startupLatch.countDown();
                _isInitialRun = false;
            } catch (Exception e) {
                LOG.info("Worker " + _name + " threw exception while waiting on barrier.");
                throw new RuntimeException(e); //NOPMD
            }
        }
        LOG.info("Worker " + _name + " started on thread " + Thread.currentThread().getName());
    }

    @Override
    public void onShutdown() {
        LOG.info("Shutting down Worker " + _name + " ");
    }

    @Override
    public void setEventReleaser(final EventReleaser eventReleaser) {
        _eventReleaser = eventReleaser;
    }

    public String getName() {
        return _name;
    }

    protected RingBuffer<E> getRingBuffer() {
        return _ringBuffer;
    }

    protected boolean isInitialRun() {
        return _isInitialRun;
    }

    protected CountDownLatch getStartupLatch() {
        return _startupLatch;
    }

    protected EventReleaser getEventReleaser(){
        return _eventReleaser;
    }


}
