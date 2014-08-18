package org.dilzio.riphttp.handlers;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;

/**
 * User: Matt C.
 * Date: 5/6/14
 *
 * Returns a 404
 */
public class NotFoundHttpRequestHandler implements HttpRequestHandler {
    @Override
    public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        response.setStatusCode(HttpStatus.SC_NOT_FOUND);
    }
}
