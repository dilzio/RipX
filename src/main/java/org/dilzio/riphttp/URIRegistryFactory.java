package org.dilzio.riphttp;

import org.apache.http.protocol.UriHttpRequestHandlerMapper;

/**
 * Builds URL Registy binding URL's and handlers.
 * @author dilzio
 *
 */
public class URIRegistryFactory {
	public UriHttpRequestHandlerMapper getURIRegistry(){
		UriHttpRequestHandlerMapper registry = new UriHttpRequestHandlerMapper();
		registry.register("/foo", new BasicOkResponseHttpRequestHandler("/foo"));
		registry.register("/foo/*/bar", new BasicOkResponseHttpRequestHandler("/foo/*/bar"));
		registry.register("/", new BasicOkResponseHttpRequestHandler("ROOT"));
		registry.register("*", new ForbiddenResponseHttpRequestHandler("FORBIDDEN"));
		registry.register("/red", new RedirectResponseHttpRequestHandler("http://www.facebook.com"));
		return registry;
	}
}
