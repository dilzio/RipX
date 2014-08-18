package org.dilzio.riphttp.util;

import com.lmax.disruptor.ExceptionHandler;

/**
 * LMAX Framework exception handler that just rethrows a RTE
 * 
 * @author dilzio
 * 
 */
public class PassthruExceptionHandler implements ExceptionHandler {

	@Override
	public void handleEventException(final Throwable ex, final long sequence, final Object event) {
		//DO NOT IMPLEMENT
	}

	@Override
	public void handleOnStartException(final Throwable ex) {
		//DO NOT IMPLEMENT
	}

	@Override
	public void handleOnShutdownException(final Throwable ex) {
		throw new RuntimeException(ex); //NOPMD
	}

}
