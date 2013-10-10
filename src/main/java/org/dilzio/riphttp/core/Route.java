package org.dilzio.riphttp.core;

import org.apache.http.protocol.HttpRequestHandler;
import org.dilzio.riphttp.util.HttpMethod;

public class Route {

	private final HttpMethod[] _supportedMethods;
	private final String _uri;
	private final HttpRequestHandler _handler;

	public Route(String uri, HttpRequestHandler handler, HttpMethod... supportedMethods) {
		_supportedMethods = supportedMethods;
		_uri = uri;
		_handler = handler;
	}

	public HttpMethod[] getSupportedMethods() {
		return _supportedMethods;
	}

	public String getUri() {
		return _uri;
	}

	public HttpRequestHandler getHandler() {
		return _handler;
	}
}
