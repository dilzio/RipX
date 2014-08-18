package org.dilzio.riphttp.core;

import org.apache.http.protocol.HttpRequestHandler;
import org.dilzio.riphttp.util.HttpMethod;

import java.util.Arrays;

public class Route {

	private final HttpMethod[] _supportedMethods;
	private final String _uri;
	private final HttpRequestHandler _handler;

	public Route(final String uri, final HttpRequestHandler handler, final HttpMethod... supportedMethods) {
        if (null == uri){ throw new IllegalArgumentException("URI argument cannot be null.");}
        if (null == handler){ throw new IllegalArgumentException("Handler argument cannot be null.");}
        if (null == supportedMethods || supportedMethods.length < 1){ throw new IllegalArgumentException("Supported Methods argument cannot be null.");}
		_supportedMethods = supportedMethods;
		_uri = uri;
		_handler = handler;
	}

	public HttpMethod[] getSupportedMethods() {
		return Arrays.copyOf(_supportedMethods, _supportedMethods.length);
	}

	public String getUri() {
		return _uri;
	}

	public HttpRequestHandler getHandler() {
		return _handler;
	}
}
