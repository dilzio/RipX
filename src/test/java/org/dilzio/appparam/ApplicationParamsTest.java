package org.dilzio.appparam;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationParamsTest {

	@Test
	public void getDefaultInt() {
		ApplicationParams params = new ApplicationParams();
		assertEquals(1234, params.getIntParam(TestProperties.INT_PROP));
	}

	@Test
	public void getDefaultBool() {
		ApplicationParams params = new ApplicationParams();
		assertEquals(false, params.getBoolParam(TestProperties.BOOL_PROP));
	}

	@Test
	public void getDefaultFloat() {
		ApplicationParams params = new ApplicationParams();
		assertEquals(2.54, params.getFloatParam(TestProperties.FLOAT_PROP), .0001);
	}

	@Test
	public void getDefaultString() {
		ApplicationParams params = new ApplicationParams();
		assertEquals("hi", params.getStringParam(TestProperties.STRING_PROP));
	}

	@Test
	public void setAndGet() {
		ApplicationParams params = new ApplicationParams();
		params.setParam(TestProperties.INT_PROP, "8888");
		params.setParam(TestProperties.BOOL_PROP, "true");
		params.setParam(TestProperties.STRING_PROP, "secret");
		params.setParam(TestProperties.FLOAT_PROP, "3.45");

		assertEquals(8888, params.getIntParam(TestProperties.INT_PROP));
		assertEquals(true, params.getBoolParam(TestProperties.BOOL_PROP));
		assertEquals(3.45, params.getFloatParam(TestProperties.FLOAT_PROP), .0001);
		assertEquals("secret", params.getStringParam(TestProperties.STRING_PROP));
	}

	@Test(expected = NumberFormatException.class)
	public void tryInvalidNumeric() {
		ApplicationParams params = new ApplicationParams();
		params.setParam(TestProperties.INT_PROP, "abcd");
		params.getIntParam(TestProperties.INT_PROP);
	}
}
