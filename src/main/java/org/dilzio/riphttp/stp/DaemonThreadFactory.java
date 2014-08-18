package org.dilzio.riphttp.stp;

import org.apache.http.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DaemonThreadFactory implements ThreadFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DaemonThreadFactory.class.getName());
    private final AtomicInteger _instanceCounter = new AtomicInteger(0);
    private final UncaughtExceptionHandler _exceptionHandler;
    private final String _namePrefix;

    public DaemonThreadFactory(final UncaughtExceptionHandler ueh, final String namePrefix) {
        Args.notNull(ueh, "exception handler cannot be null");
        _exceptionHandler = ueh;
        _namePrefix = namePrefix;
    }

    public Thread newThread(final Runnable r) {
        Thread t = new Thread(r);
        t.setUncaughtExceptionHandler(_exceptionHandler);
        t.setDaemon(true);
        String threadName = _namePrefix + "-" + _instanceCounter;
        t.setName(threadName);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Created thread: " + threadName);
        }
        _instanceCounter.getAndIncrement();
        return t;
    }
}
