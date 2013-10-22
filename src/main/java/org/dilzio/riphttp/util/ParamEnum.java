package org.dilzio.riphttp.util;

public enum ParamEnum {
	LISTEN_PORT("8081"), WORKER_COUNT("1"), RING_BUFFER_SIZE("8192"), USE_SSL("false"), SSL_KEYSTORE(null), SSL_KEYSTORE_PASSWORD(null), HANDLER_AWAIT_MLLIS("500"), TEST_FLOAT("1.35"), SERVER_NAME("RipHTTP"), SERVER_VERSION("1.0"), POOL_SHUTDOWN_AWAIT_MILLIS(
			"100"), DOCROOT("."), THREAD_POOL_SIZE(Runtime.getRuntime().availableProcessors() + ""), ONE_FOR_ONE_MAX_RESTARTS("-1"), ONE_FOR_ONE_RESTART_WINDOW_MILLIS("-1"), LISTENER_THREAD_SLEEP_ON_STARTUP_MILLIS("100");

	private String _default;

	ParamEnum(String defaultVal) {
		_default = defaultVal;
	}

	public String getDefault() {
		return _default;
	}
}
