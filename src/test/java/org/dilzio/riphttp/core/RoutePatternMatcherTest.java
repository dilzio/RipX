package org.dilzio.riphttp.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.apache.http.protocol.HttpRequestHandler;
import org.dilzio.riphttp.util.HttpMethod;
import org.junit.Test;
public class RoutePatternMatcherTest {

	@Test
	public void basicHappyPath() {
		RoutePatternMatcher  underTest = new RoutePatternMatcher();
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		underTest.register(new Route("/foo", mockHandler, HttpMethod.GET));
		HttpRequestHandler returned = underTest.lookup("/foo", HttpMethod.GET);
		assertTrue(returned == mockHandler);
	}
	
	@Test
	public void basicMultipleMethods() {
		RoutePatternMatcher  underTest = new RoutePatternMatcher();
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		underTest.register(new Route("/foo", mockHandler, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT));
		HttpRequestHandler returned = underTest.lookup("/foo", HttpMethod.GET);
		assertTrue(returned == mockHandler);
		returned = underTest.lookup("/foo", HttpMethod.POST);
		assertTrue(returned == mockHandler);
		returned = underTest.lookup("/foo", HttpMethod.PUT);
		assertTrue(returned == mockHandler);
	}
	
	@Test
	public void failsOnCorrectRouteIncorrectMethod() {
		RoutePatternMatcher  underTest = new RoutePatternMatcher();
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		underTest.register(new Route("/foo", mockHandler, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT));
		HttpRequestHandler returned = underTest.lookup("/foo", HttpMethod.HEAD);
		assertNull(returned);
	}

	@Test
	public void failsOnIncorrectRoute() {
		RoutePatternMatcher  underTest = new RoutePatternMatcher();
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		underTest.register(new Route("/foo", mockHandler, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT));
		HttpRequestHandler returned = underTest.lookup("/foo/bar", HttpMethod.HEAD);
		assertNull(returned);
	}

	@Test
	public void failsOnNullRoute() {
		RoutePatternMatcher  underTest = new RoutePatternMatcher();
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		underTest.register(new Route(null, mockHandler, HttpMethod.PUT));
	}
}
