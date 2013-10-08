package org.dilzio.riphttp;

import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.dilzio.riphttp.core.RipHttp;
import org.dilzio.riphttp.handlers.BasicOkResponseHttpRequestHandler;
import org.dilzio.riphttp.handlers.ForbiddenResponseHttpRequestHandler;
import org.dilzio.riphttp.handlers.RedirectResponseHttpRequestHandler;
import org.dilzio.riphttp.handlers.WriteACookieResponseHttpRequestHandler;

/**
 * 
 * @author dilzio
 */
public class Main {
	/**
	 * Main class for running RipHttp as a standalone server
	 * @param args arg[0] should be the path to a java properties file where each property is a name value pair.
	 * These are read into an ApplicationParams class for further processing.  See the ParamEnum class for 
	 * the list of available options.
	 * 
	 */
	//TODO remove route hardcoding, source properties file correctly
	public static void main(String[] args) {
		UriHttpRequestHandlerMapper registry = new UriHttpRequestHandlerMapper();
		registry.register("/foo", new BasicOkResponseHttpRequestHandler("/foo"));
		registry.register("/foo/*/bar", new BasicOkResponseHttpRequestHandler("/foo/*/bar"));
		registry.register("/", new BasicOkResponseHttpRequestHandler("ROOT"));
		registry.register("*", new ForbiddenResponseHttpRequestHandler("FORBIDDEN"));
		registry.register("/red", new RedirectResponseHttpRequestHandler("http://www.facebook.com"));
		registry.register("/cookie", new WriteACookieResponseHttpRequestHandler());
		
		if (args.length == 0){
			new RipHttp(registry).start();
		}
		//TODO: source properties file
	}

}
