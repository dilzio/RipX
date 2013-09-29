package org.dilzio.riphttp;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public class WriteACookieResponseHttpRequestHandler implements HttpRequestHandler {

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		response.addHeader(new BasicHeader("Set-Cookie","mycookie1=ILIKECOOKIE; Expires=Saturday, August 27, 2203 8:16:28 PM"));
		response.setEntity(new StringEntity("wrote a cookie"));
	}

}
