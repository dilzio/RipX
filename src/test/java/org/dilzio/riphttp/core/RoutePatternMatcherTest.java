package org.dilzio.riphttp.core;

import org.apache.http.protocol.HttpRequestHandler;
import org.dilzio.riphttp.util.HttpMethod;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class RoutePatternMatcherTest {

	private static final String FOO = "/foo";

	@Test
	public void basicHappyPath() {
		RoutePatternMatcher underTest = new RoutePatternMatcher();
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		underTest.register(new Route(FOO, mockHandler, HttpMethod.GET));
		HttpRequestHandler returned = underTest.lookup(FOO, HttpMethod.GET);
		assertTrue(returned.equals(mockHandler));
	}

	@Test
	public void basicMultipleMethods() {
		RoutePatternMatcher underTest = new RoutePatternMatcher();
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		underTest.register(new Route(FOO, mockHandler, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT));
		HttpRequestHandler returned = underTest.lookup(FOO, HttpMethod.GET);
		assertTrue(returned.equals(mockHandler));
		returned = underTest.lookup(FOO, HttpMethod.POST);
		assertTrue(returned.equals(mockHandler));
		returned = underTest.lookup(FOO, HttpMethod.PUT);
		assertTrue(returned.equals(mockHandler));
	}

	@Test
	public void failsOnCorrectRouteIncorrectMethod() {
		RoutePatternMatcher underTest = new RoutePatternMatcher();
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		underTest.register(new Route(FOO, mockHandler, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT));
		HttpRequestHandler returned = underTest.lookup(FOO, HttpMethod.HEAD);
		assertNull(returned);
	}

	@Test
	public void failsOnIncorrectRoute() {
		RoutePatternMatcher underTest = new RoutePatternMatcher();
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		underTest.register(new Route(FOO, mockHandler, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT));
		HttpRequestHandler returned = underTest.lookup("/foo/bar", HttpMethod.HEAD);
		assertNull(returned);
	}

	@Test(expected = IllegalArgumentException.class)
	public void failsOnNullRoute() {
		RoutePatternMatcher underTest = new RoutePatternMatcher();
		HttpRequestHandler mockHandler = mock(HttpRequestHandler.class);
		underTest.register(new Route(null, mockHandler, HttpMethod.PUT));
	}
}
