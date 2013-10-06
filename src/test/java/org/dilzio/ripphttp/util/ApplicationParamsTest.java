package org.dilzio.ripphttp.util;

import static org.junit.Assert.*;

import org.dilzio.riphttp.util.ApplicationParams;
import org.dilzio.riphttp.util.ParamEnum;
import org.junit.Test;

public class ApplicationParamsTest {

	@Test
	public void getDefaultInt() {
		ApplicationParams params = new ApplicationParams();
		assertEquals(8081, params.getIntParam(ParamEnum.LISTEN_PORT));
	}

	@Test
	public void getDefaultBool() {
		ApplicationParams params = new ApplicationParams();
		assertEquals(false, params.getBoolParam(ParamEnum.USE_SSL));
	}

	@Test
	public void getDefaultFloat() {
		ApplicationParams params = new ApplicationParams();
		assertEquals(1.35, params.getFloatParam(ParamEnum.TEST_FLOAT), .0001);
	}

	@Test
	public void getDefaultString() {
		ApplicationParams params = new ApplicationParams();
		assertNull(params.getStringParam(ParamEnum.SSL_KEYSTORE_PASSWORD));
	}
	
	@Test
	public void setAndGet() {
		ApplicationParams params = new ApplicationParams();
		params.setParam(ParamEnum.LISTEN_PORT, "8888");
		params.setParam(ParamEnum.USE_SSL, "true");
		params.setParam(ParamEnum.SSL_KEYSTORE_PASSWORD, "secret");
		params.setParam(ParamEnum.TEST_FLOAT, "3.45");

		assertEquals(8888, params.getIntParam(ParamEnum.LISTEN_PORT));
		assertEquals(true, params.getBoolParam(ParamEnum.USE_SSL));
		assertEquals(3.45, params.getFloatParam(ParamEnum.TEST_FLOAT), .0001);
		assertEquals("secret", params.getStringParam(ParamEnum.SSL_KEYSTORE_PASSWORD));
	}
}
