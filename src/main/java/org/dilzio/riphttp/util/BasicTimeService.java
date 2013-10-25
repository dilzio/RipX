package org.dilzio.riphttp.util;

/**
 * Default ITimeService implementation
 * @author dilzio
 *
 */
public class BasicTimeService implements ITimeService {

	@Override
	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}

}
