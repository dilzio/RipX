package org.dilzio.riphttp;

import com.lmax.disruptor.EventFactory;

public final class HttpConnectionEvent {
	
	private long _tempVal = 0;
	
	public long get_tempVal() {
		return _tempVal;
	}
	public void set_tempVal(long _tempVal) {
		this._tempVal = _tempVal;
	}
	public static final EventFactory<HttpConnectionEvent> EVENT_FACTORY = new EventFactory<HttpConnectionEvent>()
	{

		@Override
		public HttpConnectionEvent newInstance() {
			return new HttpConnectionEvent();
		}
		
	};
}
