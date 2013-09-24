package org.dilzio.riphttp;

import com.lmax.disruptor.RingBuffer;

public class ListenerDaemon implements Runnable {

	private final int _port;
	private final RingBuffer<HttpConnectionEvent> _ringBuffer;
	public ListenerDaemon(final int port, final RingBuffer<HttpConnectionEvent> ringBuffer) {
		_port = port;
		_ringBuffer = ringBuffer;
	}

	@Override
	public void run() {
		while(true){
			long sequence = _ringBuffer.next();
			_ringBuffer.get(sequence).set_tempVal(sequence);
			_ringBuffer.publish(sequence);
		}

	}

}
