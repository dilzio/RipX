package org.dilzio.riphttp.core;

import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.protocol.HttpRequestHandler;
import org.dilzio.riphttp.util.HttpMethod;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class RouteHttpRequestHandlerMapperTest {

	@Test
	public void basicHappyPath() {
		RoutePatternMatcher mockMatcher = mock(RoutePatternMatcher.class);
		RouteHttpRequestHandlerMapper underTest = new RouteHttpRequestHandlerMapper(mockMatcher);
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("GET", "/foo", new ProtocolVersion("HTTP", 1, 1)));
		underTest.lookup(request);
		verify(mockMatcher).lookup("/foo", HttpMethod.GET);
	}

	@Test
	public void ignoresQueryParams() {
		RoutePatternMatcher mockMatcher = mock(RoutePatternMatcher.class);
		RouteHttpRequestHandlerMapper underTest = new RouteHttpRequestHandlerMapper(mockMatcher);
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("GET", "/foo?bar=baz", new ProtocolVersion("HTTP", 1, 1)));
		underTest.lookup(request);
		verify(mockMatcher).lookup("/foo", HttpMethod.GET);
	}

	@Test
	public void ignoreHash() {
		RoutePatternMatcher mockMatcher = mock(RoutePatternMatcher.class);
		RouteHttpRequestHandlerMapper underTest = new RouteHttpRequestHandlerMapper(mockMatcher);
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("GET", "/foo#asection", new ProtocolVersion("HTTP", 1, 1)));
		underTest.lookup(request);
		verify(mockMatcher).lookup("/foo", HttpMethod.GET);
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
		
		when(mockMatcher.lookup("/foo", HttpMethod.GET)).thenReturn(mockHandler);
		underTest.register(new Route("/foo", mockHandler, HttpMethod.GET));
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("GET", "/foo#asection", new ProtocolVersion("HTTP", 1, 1)));
		HttpRequestHandler returned = underTest.lookup(request);
		assertTrue(mockHandler == returned);
	}
	
	@Test
	public void rpmIntegrationTest(){
		RoutePatternMatcher matcher = new RoutePatternMatcher();
		RouteHttpRequestHandlerMapper underTest = new RouteHttpRequestHandlerMapper(matcher);
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		HttpRequestHandler mockHandler2 = mock(HttpRequestHandler.class);
		HttpRequestHandler mockHandler3 = mock(HttpRequestHandler.class);
		HttpRequestHandler mockHandler4 = mock(HttpRequestHandler.class);
		
		underTest.register(new Route("/foo*", mockHandler, HttpMethod.GET));
		underTest.register(new Route("/foo/bar", mockHandler2, HttpMethod.GET));
		underTest.register(new Route("*", mockHandler3, HttpMethod.GET));
		underTest.register(new Route("*/foo", mockHandler4, HttpMethod.GET));
		
		HttpRequest request = new BasicHttpRequest(new BasicRequestLine("GET", "/foo", new ProtocolVersion("HTTP", 1, 1)));
		HttpRequestHandler returned = underTest.lookup(request);
		assertTrue(mockHandler == returned);

		request = new BasicHttpRequest(new BasicRequestLine("GET", "/foo/bar", new ProtocolVersion("HTTP", 1, 1)));
		returned = underTest.lookup(request);
		assertTrue(mockHandler2 == returned);
		
		request = new BasicHttpRequest(new BasicRequestLine("GET", "/bilz", new ProtocolVersion("HTTP", 1, 1)));
		returned = underTest.lookup(request);
		assertTrue(mockHandler3 == returned);

		request = new BasicHttpRequest(new BasicRequestLine("GET", "/frat/foo", new ProtocolVersion("HTTP", 1, 1)));
		returned = underTest.lookup(request);
		assertTrue(mockHandler4 == returned);
	}
}
