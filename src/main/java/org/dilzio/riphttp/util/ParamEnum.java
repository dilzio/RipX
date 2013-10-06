package org.dilzio.riphttp.util;

public enum ParamEnum {
	LISTEN_PORT("8081"),
	WORKER_COUNT("1"),
	RING_BUFFER_SIZE("8192"),
	USE_SSL("false"),
	SSL_KEYSTORE(null),
	SSL_KEYSTORE_PASSWORD(null),
	HANDLER_AWAIT_MLLIS("500"),
	TEST_FLOAT("1.35");
	
	private String _default;
	
	ParamEnum(String defaultVal){
		_default = defaultVal;
	}
	
	public String getDefault(){
		return _default;
	}
}
