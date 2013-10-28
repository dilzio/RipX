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

	@Override
	public long nanoTime() {
		return System.nanoTime();
	}

	@Override
	public long microTime() {
		return nanoTime() / 1000l;
	}

}
