package org.dilzio.riphttp;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
public class WriteACookieResponseHttpRequestHandler implements HttpRequestHandler {
    private final static AtomicInteger COUNTER = new AtomicInteger();
	private final CookieUtil _cookieUtil = new CookieUtil();
	
	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		Map<String, Cookie> cookies = _cookieUtil.getCookiesFromRequest(request);
		response.addHeader(_cookieUtil.getCookieHeader("mynewcook", "secretcodeval" + COUNTER.getAndIncrement(),null, "nil", 
						  false, false));
		for (String key : cookies.keySet()){
			Cookie cookie = cookies.get(key);
			response.setEntity(new StringEntity(String.format("Got cookie name: %s value: %s", cookie.getName(), cookie.getValue())));
		}
	}

}
