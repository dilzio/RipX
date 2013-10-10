package org.dilzio.riphttp.handlers;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public class BasicOkResponseHttpRequestHandler implements HttpRequestHandler {
	private final String _message;

	public BasicOkResponseHttpRequestHandler(final String message) {
		_message = message;
	}

	@Override
	public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
		// response.setStatusCode(HttpStatus.SC_OK);
		response.setEntity(new StringEntity(_message));
	}

}
