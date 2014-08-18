package org.dilzio.appparam;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationParamsFactoryTest {

	private static final String INT_PROP = "INT_PROP";
	private static final String TESTFILE1 = "./src/test/resources/org/dilzio/rippinhttp/util/testprops1.properties";

	@Test
	public void hydrateWithParams() {
        ApplicationParamsFactory _underTest = ApplicationParamsFactory.getInstance();
		String path = TESTFILE1;
		boolean overlayEnvVars = false;
		ApplicationParams<TestProperties> params = _underTest.newParams(TestProperties.values(), path, overlayEnvVars);
		assertEquals(556, params.getIntParam(TestProperties.INT_PROP));
        assertEquals(true, params.getBoolParam(TestProperties.BOOL_PROP));
        assertEquals("yoyo", params.getStringParam(TestProperties.STRING_PROP));
        assertEquals(666.666, params.getFloatParam(TestProperties.FLOAT_PROP), .001);
	}
	
	@Test
	public void hydrateWithParamsOverlayTrue() {
		ApplicationParamsFactory _underTest = ApplicationParamsFactory.getInstance();
		String path = TESTFILE1;
		boolean overlayEnvVars = true;
        ApplicationParams<TestProperties> params = _underTest.newParams(TestProperties.values(), path, overlayEnvVars);

        assertEquals(556, params.getIntParam(TestProperties.INT_PROP));
        assertEquals(true, params.getBoolParam(TestProperties.BOOL_PROP));
        assertEquals("yoyo", params.getStringParam(TestProperties.STRING_PROP));
        assertEquals(666.666, params.getFloatParam(TestProperties.FLOAT_PROP), .001);
	}

	@Test
	public void hydrateWithParamsEnvOverride() {
	    System.setProperty(INT_PROP, "6666");
		ApplicationParamsFactory _underTest = ApplicationParamsFactory.getInstance();
		String path = TESTFILE1;
		boolean overlayEnvVars = true;
        ApplicationParams<TestProperties> params = _underTest.newParams(TestProperties.values(), path, overlayEnvVars);

        assertEquals(6666, params.getIntParam(TestProperties.INT_PROP));
        assertEquals(true, params.getBoolParam(TestProperties.BOOL_PROP));
        assertEquals("yoyo", params.getStringParam(TestProperties.STRING_PROP));
        assertEquals(666.666, params.getFloatParam(TestProperties.FLOAT_PROP), .001);
	    System.clearProperty(INT_PROP);
	}
	
	@Test
	public void hydrateFromEnvironment() {
	    System.setProperty("INT_PROP", "6666");
	    System.setProperty("BOOL_PROP", "true");
	    System.setProperty("STRING_PROP", "UnitTest");
        System.setProperty("FLOAT_PROP", "3.45");
		ApplicationParamsFactory _underTest = ApplicationParamsFactory.getInstance();
        ApplicationParams<TestProperties> params = _underTest.newParams(TestProperties.values(), null, true);
        assertEquals(6666, params.getIntParam(TestProperties.INT_PROP));
        assertEquals(true, params.getBoolParam(TestProperties.BOOL_PROP));
        assertEquals("UnitTest", params.getStringParam(TestProperties.STRING_PROP));
        assertEquals(3.45, params.getFloatParam(TestProperties.FLOAT_PROP), .001);
	    System.clearProperty("INT_PROP");
        System.clearProperty("BOOL_PROP");
        System.clearProperty("STRING_PROP");
        System.clearProperty("FLOAT_PROP");
	}
}
