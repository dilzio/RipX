package org.dilzio.riphttp.core;

import org.dilzio.riphttp.handlers.HttpFileHandler;
import org.dilzio.riphttp.util.HttpMethod;
import org.junit.Test;

public class RipHttpIntegrationTest {

	@Test
	public void basicHappyPath() throws Exception {
		RipHttp underTest = new RipHttp();
		underTest.addHandlers(new Route("*", new HttpFileHandler("/tmp"), HttpMethod.GET));
		for (int i = 0; i < 4; i++) {
			underTest.start();
			underTest.stop();
		}
	}
}
