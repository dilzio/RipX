package org.dilzio.riphttp.appparam;

import static org.junit.Assert.assertEquals;

import org.dilzio.riphttp.util.ParamEnum;
import org.dilzio.ripphttp.appparam.ApplicationParamsFactory;
import org.dilzio.ripphttp.util.stp.ApplicationParams;
import org.junit.Test;

public class ApplicationParamsFactoryTest {

	private static final String LISTEN_PORT = "LISTEN_PORT";
	private static final String TESTFILE1 = "./src/test/resources/org/dilzio/rippinhttp/util/testprops1.properties";

	@Test
	public void hydrateWithParams() {
		ApplicationParamsFactory _underTest = ApplicationParamsFactory.getInstance();
		String path = TESTFILE1;
		boolean overlayEnvVars = false;
		ApplicationParams params = _underTest.newParams(path, overlayEnvVars);
		assertEquals(12345, params.getIntParam(ParamEnum.LISTEN_PORT));
		assertEquals(44, params.getIntParam(ParamEnum.WORKER_COUNT));
		assertEquals(7998, params.getIntParam(ParamEnum.RING_BUFFER_SIZE));
	}
	
	@Test
	public void hydrateWithParamsOveralayTrue() {
		ApplicationParamsFactory _underTest = ApplicationParamsFactory.getInstance();
		String path = TESTFILE1;
		boolean overlayEnvVars = true;
		ApplicationParams params = _underTest.newParams(path, overlayEnvVars);
		assertEquals(12345, params.getIntParam(ParamEnum.LISTEN_PORT));
		assertEquals(44, params.getIntParam(ParamEnum.WORKER_COUNT));
		assertEquals(7998, params.getIntParam(ParamEnum.RING_BUFFER_SIZE));
	}
	
	@Test
	public void hydrateWithParamsEnvOverride() {
	    System.setProperty(LISTEN_PORT, "6666");
		ApplicationParamsFactory _underTest = ApplicationParamsFactory.getInstance();
		String path = TESTFILE1;
		boolean overlayEnvVars = true;
		ApplicationParams params = _underTest.newParams(path, overlayEnvVars);
		assertEquals(6666, params.getIntParam(ParamEnum.LISTEN_PORT));
		assertEquals(44, params.getIntParam(ParamEnum.WORKER_COUNT));
		assertEquals(7998, params.getIntParam(ParamEnum.RING_BUFFER_SIZE));
	    System.clearProperty(LISTEN_PORT);
	}
	
	@Test
	public void hydrateFromEnvironment() {
	    System.setProperty(LISTEN_PORT, "6666");
	    System.setProperty("USE_SSL", "true");
	    System.setProperty("SERVER_NAME", "UnitTest");
		ApplicationParamsFactory _underTest = ApplicationParamsFactory.getInstance();
		ApplicationParams params = _underTest.newParamsFromEnvironment();
		assertEquals(6666, params.getIntParam(ParamEnum.LISTEN_PORT));
		assertEquals(true, params.getBoolParam(ParamEnum.USE_SSL));
		assertEquals("UnitTest", params.getStringParam(ParamEnum.SERVER_NAME));
	    System.clearProperty(LISTEN_PORT);
	    System.clearProperty("USE_SSL");
	    System.clearProperty("SERVER_NAME");
	}
}
