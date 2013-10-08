package org.dilzio.riphttp.core;

import org.apache.http.protocol.HttpRequestHandler;
import org.dilzio.riphttp.util.HttpMethod;

public class Route {

	private final HttpMethod _method;
	private final String _uri;
	private final HttpRequestHandler _handler;

	public Route(HttpMethod method, String uri, HttpRequestHandler handler) {
		_method = method;
		_uri = uri;
		_handler = handler;
	}
	
	public HttpMethod getMethod(){
		return _method;
	}

	public String getUri(){
		return _uri;
	}

	public HttpRequestHandler getHandler(){
		return _handler;
	}
}
