package org.dilzio.riphttp;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpServerConnection;

import com.lmax.disruptor.EventFactory;

public final class HttpConnectionEvent {
	
	private HttpServerConnection _httpConn;
	private final long _id;
	
	public HttpConnectionEvent(final long id){
		_id = id;
	}
	
	public HttpServerConnection get_httpConn() {
		return _httpConn;
	}

	public void set_httpConn(HttpServerConnection _httpConn) {
		this._httpConn = _httpConn;
	}

	public long getId(){
		return _id;
	}
	public static final EventFactory<HttpConnectionEvent> EVENT_FACTORY = new EventFactory<HttpConnectionEvent>()
	{
		private AtomicLong _idGenerator = new AtomicLong();
		@Override
		public HttpConnectionEvent newInstance() {
			return new HttpConnectionEvent(_idGenerator.getAndIncrement());
		}
		
	};
}
