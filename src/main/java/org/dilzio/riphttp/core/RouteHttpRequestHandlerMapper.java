package org.dilzio.riphttp.core;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerMapper;
import org.apache.http.util.Args;
import org.dilzio.riphttp.util.HttpMethod;

public class RouteHttpRequestHandlerMapper implements HttpRequestHandlerMapper {

	final RoutePatternMatcher _routeMatcher;

	public RouteHttpRequestHandlerMapper(final RoutePatternMatcher routeMatcher) {
		Args.notNull(routeMatcher, "Constructed with null route matcher");
		_routeMatcher = routeMatcher;
	}

	@Override
	public HttpRequestHandler lookup(final HttpRequest request) {
		return _routeMatcher.lookup(getRequestPath(request), getRequestMethod(request));
	}

	private HttpMethod getRequestMethod(final HttpRequest request) {
		return HttpMethod.valueOf(request.getRequestLine().getMethod());
	}

	private String getRequestPath(final HttpRequest request) {
		String uriPath = request.getRequestLine().getUri();
		int index = uriPath.indexOf('?');
		if (index == -1) {
			index = uriPath.indexOf('#');
			if (index != -1) {
				uriPath = uriPath.substring(0, index);
			}
		} else {
			uriPath = uriPath.substring(0, index);
		}

		return uriPath;
	}

	public void register(final Route r) {
		_routeMatcher.register(r);
	}

}
