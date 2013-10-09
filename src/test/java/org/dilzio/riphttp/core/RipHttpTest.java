package org.dilzio.riphttp.core;

import org.dilzio.riphttp.handlers.BasicOkResponseHttpRequestHandler;
import org.dilzio.riphttp.util.HttpMethod;
import org.junit.Test;

public class RipHttpTest {

	@Test
	public void test() {
		RipHttp underTest = new RipHttp();

		underTest.addHandlers(new Route("*", new BasicOkResponseHttpRequestHandler("testio"), HttpMethod.GET));
	}

}
