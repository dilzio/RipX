package org.dilzio.riphttp.handlers;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;

public class RedirectResponseHttpRequestHandler implements HttpRequestHandler {
	private final String _location;

	public RedirectResponseHttpRequestHandler(final String location) {
		if (null == location) {
			throw new IllegalArgumentException("Must Provide Location");
		}
		_location = location;
	}

	@Override
	public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
		response.setStatusCode(HttpStatus.SC_MOVED_PERMANENTLY);
		response.addHeader(new BasicHeader("Refresh", "0; url=" + _location));
	}

}
