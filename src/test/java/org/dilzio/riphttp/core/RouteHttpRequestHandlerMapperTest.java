package org.dilzio.riphttp.core;

import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.protocol.HttpRequestHandler;
import org.dilzio.riphttp.util.HttpMethod;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RouteHttpRequestHandlerMapperTest {

	@Test
	public void basicHappyPath() {
		RoutePatternMatcher mockMatcher = mock(RoutePatternMatcher.class);
		RouteHttpRequestHandlerMapper underTest = new RouteHttpRequestHandlerMapper(mockMatcher);
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("GET", "/foo", new ProtocolVersion("HTTP", 1, 1)));
		underTest.lookup(request);
		verify(mockMatcher).lookup(HttpMethod.GET, "/foo");
	}

	@Test
	public void ignoresQueryParams() {
		RoutePatternMatcher mockMatcher = mock(RoutePatternMatcher.class);
		RouteHttpRequestHandlerMapper underTest = new RouteHttpRequestHandlerMapper(mockMatcher);
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("GET", "/foo?bar=baz", new ProtocolVersion("HTTP", 1, 1)));
		underTest.lookup(request);
		verify(mockMatcher).lookup(HttpMethod.GET, "/foo");
	}

	@Test
	public void ignoreHash() {
		RoutePatternMatcher mockMatcher = mock(RoutePatternMatcher.class);
		RouteHttpRequestHandlerMapper underTest = new RouteHttpRequestHandlerMapper(mockMatcher);
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("GET", "/foo#asection", new ProtocolVersion("HTTP", 1, 1)));
		underTest.lookup(request);
		verify(mockMatcher).lookup(HttpMethod.GET, "/foo");
	}

	@Test(expected=IllegalArgumentException.class)
	public void nullMatcherOnConstruction() {
		new RouteHttpRequestHandlerMapper(null);
	}
	
	@Test
	public void registerHandler(){
		RoutePatternMatcher mockMatcher = mock(RoutePatternMatcher.class);
		RouteHttpRequestHandlerMapper underTest = new RouteHttpRequestHandlerMapper(mockMatcher);
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		
		when(mockMatcher.lookup(HttpMethod.GET, "/foo")).thenReturn(mockHandler);
		underTest.register(new Route(HttpMethod.GET, "/foo", mockHandler));
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("GET", "/foo#asection", new ProtocolVersion("HTTP", 1, 1)));
		HttpRequestHandler returned = underTest.lookup(request);
		assert(mockHandler == returned);
	}
}
