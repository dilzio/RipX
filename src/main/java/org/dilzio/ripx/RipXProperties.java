package org.dilzio.ripx;

import org.dilzio.appparam.DefaultingEnum;

/**
 * User: Matt C.
 * Date: 3/18/14
 *
 * Properties Enum for RipHttp
 */
public enum RipXProperties implements DefaultingEnum {
    WORKER_COUNT("1"),
    RING_BUFFER_SIZE("8192"),
    STARTUP_TIMEOUT_MS("1000"),
    SERVER_NAME("RipHTTP"),
    SERVER_VERSION("1.0"),
    POOL_SHUTDOWN_AWAIT_MILLIS("100"),
    THREAD_POOL_SIZE(Integer.toString(Runtime.getRuntime().availableProcessors())),
    ONE_FOR_ONE_MAX_RESTARTS("-1"),
    ONE_FOR_ONE_RESTART_WINDOW_MILLIS("-1"),
    LISTENER_THREAD_SLEEP_ON_STARTUP_MILLIS("100"),
    NUM_LISTENERS("1"),
    WORKER_THREAD_NAME_PREFIX("worker");

    private final String _default;

    RipXProperties(final String defaultValue){
        _default = defaultValue;
    }

    @Override
    public String getDefault() {
        return _default;
    }
}
