package org.dilzio.riphttp.core;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerMapper;
import org.apache.http.util.Args;
import org.dilzio.riphttp.util.HttpMethod;

public class RouteHttpRequestHandlerMapper implements HttpRequestHandlerMapper {
	
	final RoutePatternMatcher _routeMatcher;
	
	public RouteHttpRequestHandlerMapper(RoutePatternMatcher routeMatcher){
		Args.notNull(routeMatcher, "Constructed with null route matcher");
		_routeMatcher = routeMatcher;
	}

	@Override
	public HttpRequestHandler lookup(HttpRequest request) {
	     return _routeMatcher.lookup(getRequestPath(request), getRequestMethod(request));
	}

	private HttpMethod getRequestMethod(HttpRequest request) {
		HttpMethod method = HttpMethod.valueOf(request.getRequestLine().getMethod());
		return method;
	}
	
	private String getRequestPath(HttpRequest request) {
		String uriPath = request.getRequestLine().getUri();
        int index = uriPath.indexOf("?");
        if (index != -1) {
            uriPath = uriPath.substring(0, index);
        } else {
            index = uriPath.indexOf("#");
            if (index != -1) {
                uriPath = uriPath.substring(0, index);
            }
        }
        return uriPath;
	}
	
	public void register(Route r) {
		_routeMatcher.register(r);
	}

}
