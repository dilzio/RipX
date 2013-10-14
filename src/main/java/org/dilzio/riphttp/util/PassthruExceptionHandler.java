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
	public void handleEventException(Throwable ex, long sequence, Object event) {
		throw new RuntimeException(ex);
	}

	@Override
	public void handleOnStartException(Throwable ex) {
		throw new RuntimeException(ex);
	}

	@Override
	public void handleOnShutdownException(Throwable ex) {
		throw new RuntimeException(ex);
	}

}
