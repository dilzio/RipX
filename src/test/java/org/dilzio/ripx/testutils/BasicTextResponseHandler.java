package org.dilzio.ripx.testutils;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dilzio
 * Date: 11/27/13
 */
public class BasicTextResponseHandler implements HttpRequestHandler {
    private final String _bodyTxt;

    public BasicTextResponseHandler(final String bodyTxt) {
        _bodyTxt = bodyTxt;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        response.setEntity(new StringEntity(_bodyTxt));
    }
}
