package org.dilzio.ripx;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventReleaser;
import com.lmax.disruptor.RingBuffer;
import org.dilzio.appparam.ApplicationParams;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * User: Matt C.
 * Date: 3/12/14
 */
public class RipXIntegrationTest {

    private static final int MAXITERS = 1000;
    private boolean _finished = false;

    @Test
    public void TestConstruction() throws InterruptedException {

        ApplicationParams appParams = getApplicationParams();
        appParams.setParam(RipXProperties.NUM_LISTENERS, "2");
        appParams.setParam(RipXProperties.WORKER_COUNT, "5");
        appParams.setParam(RipXProperties.THREAD_POOL_SIZE, "10");
        RipXWorkerFactory<TestRipXWorker, TestRipXEvent> workerFactory = getWorkerFactory();
        RipXProducerFactory producerFactory = getProducerFactory();
        TestRipXEventFactory eventFactory = new TestRipXEventFactory();
        RipX<TestRipXEvent, TestRipXWorker> underTest = new RipX<>(appParams, eventFactory, workerFactory, producerFactory);
        underTest.start();

        while (!_finished) {
            Thread.sleep(1000);
        }

    }

    private RipXProducerFactory getProducerFactory() {
        return new TestProducerFactory();
    }

    private RipXWorkerFactory<TestRipXWorker, TestRipXEvent> getWorkerFactory() {
        return new TestWorkerFactory();
    }

    private ApplicationParams getApplicationParams() {
        return new ApplicationParams<RipXProperties>();
    }


    ///Test Implementation Classes (typically these would be in external files)/////
    public static class TestRipXEventFactory implements EventFactory<TestRipXEvent> {
        @Override
        public TestRipXEvent newInstance() {
            return new TestRipXEvent();
        }
    }

    ;

    private static class TestRipXEvent {
        private int _value1;
        private int _value2;

        public int getValue2() {
            return _value2;
        }

        public void setValue2(int value2) {
            _value2 = value2;
        }

        public int getValue1() {
            return _value1;
        }

        public void setValue1(int value1) {
            _value1 = value1;
        }
    }

    private class TestRipXWorker extends AbstractRipXWorker<TestRipXEvent> {
        private final int _id;

        public TestRipXWorker(final RingBuffer<TestRipXEvent> ringBuffer, final int i) {
            super(ringBuffer, new CountDownLatch(0),  "testworker");
            _id = i;
        }

        @Override
        public void setEventReleaser(EventReleaser eventReleaser) {

        }

        @Override
        public void onStart() {

        }

        @Override
        public void onShutdown() {

        }

        @Override
        public void onEvent(TestRipXEvent event) throws Exception {
            //System.out.println("Worker " + _id + " value1: " + event.getValue1());
            //System.out.println("Worker " + _id + " value2: " + event.getValue2());

            if (event.getValue1() > MAXITERS) {
                _finished = true;
            }
        }
    }

    private class TestProducerFactory implements RipXProducerFactory<TestRipXEvent> {
        @Override
        public Collection<? extends RipXProducer> getProducerCollection(RingBuffer<TestRipXEvent> ringBuffer, int numConfiguredListeners) {
            List<TestRipXProducer> producerList = new LinkedList<>();

            for (int i = 0; i < numConfiguredListeners; i++) {
                producerList.add(new TestRipXProducer(ringBuffer));
            }

            return producerList;
        }
    }

    private class TestWorkerFactory implements RipXWorkerFactory<TestRipXWorker, TestRipXEvent> {
        @Override
        public TestRipXWorker[] getWorkerArray(final int numWorkers, final RingBuffer<TestRipXEvent> ringBuffer) {

            TestRipXWorker[] workerArray = new TestRipXWorker[numWorkers];
            for (int i = 0; i < numWorkers; i++) {
                workerArray[i] = new TestRipXWorker(ringBuffer, i);
            }
            return workerArray;
        }

        @Override
        public void injectStartupBarrier(CountDownLatch startupLatch) {

        }
    }

    private class TestRipXProducer implements RipXProducer {
        private final RingBuffer<TestRipXEvent> _ringBuffer;

        public TestRipXProducer(final RingBuffer<TestRipXEvent> ringBuffer) {
            _ringBuffer = ringBuffer;
        }

        @Override
        public void stop() {

        }

        @Override
        public void run() {
            int val1counter = 0;
            int val2counter = 10;

            while (!_finished) {
                long sequence = Long.MIN_VALUE;
                try {
                    sequence = _ringBuffer.next();
                    TestRipXEvent event = _ringBuffer.get(sequence);
                    event.setValue1(val1counter);
                    event.setValue2(val2counter);
                    val1counter++;
                    val2counter++;
                } finally {
                    // always publish event if ringbuffer sequence was incremented with _ringBuffer.next()
                    if (sequence != Long.MIN_VALUE) {
                        _ringBuffer.publish(sequence);
                    }
                }
            }
        }
    }
}
