package org.dilzio.riphttp.core;

import org.apache.http.protocol.HttpRequestHandler;
import org.dilzio.riphttp.util.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class RoutePatternMatcher {

	private final Map<String, Map<HttpMethod, HttpRequestHandler>> _routeMap = new HashMap<>();

	public void register(final Route route) {
		HashMap<HttpMethod, HttpRequestHandler> mapForRoute = new HashMap<>();
		HttpMethod[] supportedMethods = route.getSupportedMethods();

		for (HttpMethod method : supportedMethods) {
			mapForRoute.put(method, route.getHandler());
		}

		_routeMap.put(route.getUri(), mapForRoute);
	}

	/**
	 * Find a configured handler for a given request URI and HTTP Method. First
	 * attempts to do a direct lookup with the given URI. If that fails, it will
	 * look for a route with a suitable wildcarded URI pattern configured. If no
	 * route is identified then a null is returned.
	 * 
	 * If a route is found, an additional filter is applied to see whether the
	 * request's HTTPMethod is configured for the route if it is, the route's
	 * HttpHandler is returned, else a null value is returned.
	 * 
	 * @param requestUri
	 * @param method
	 * @return a HttpHandler impl or null
	 */
    @SuppressWarnings("PMD.CyclomaticComplexity")
	public HttpRequestHandler lookup(final String requestUri, final HttpMethod method) {
		Map<HttpMethod, HttpRequestHandler> methodToHandlerMap = _routeMap.get(requestUri);
		if (null == methodToHandlerMap) {
			String bestMatch = null;
			for (final String pattern : _routeMap.keySet()) {
				if (matchUriRequestPattern(pattern, requestUri) && (bestMatch == null || bestMatch.length() < pattern.length() || bestMatch.length() == pattern.length() && pattern.endsWith("*"))) {
					methodToHandlerMap = _routeMap.get(pattern);
					bestMatch = pattern;
				}
			}

			if (null == bestMatch) { // no matching route found
				return null;
			}

			if (!methodToHandlerMap.keySet().contains(method)) { // matching
																	// route
																	// found,
																	// but
																	// request
																	// method
																	// not
																	// configured
																	// for route
				return null;
			}

			return methodToHandlerMap.get(method);
		} else {
			return methodToHandlerMap.get(method);
		}
	}

	private boolean matchUriRequestPattern(final String pattern, final String requestUri) {
		if ("*".equals(pattern)) {
			return true;
		} else {
			return pattern.endsWith("*") && requestUri.startsWith(pattern.substring(0, pattern.length() - 1)) || pattern.startsWith("*") && requestUri.endsWith(pattern.substring(1, pattern.length())); //NOPMD
		}
	}
}
