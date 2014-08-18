package org.dilzio.riphttp.util;

/**
 * Wrapper to decouple application from system based time functions.  Implementations can return real or simulated time.
 * Assists with test simulations, or any other situation when we want to speed up or slow down time.
 * @author dilzio
 *
 */
public interface ITimeService {

	long currentTimeMillis();
	long nanoTime();
	long microTime();

}
