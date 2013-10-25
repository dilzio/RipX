package org.dilzio.riphttp.core;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpServerConnection;

import com.lmax.disruptor.EventFactory;

public final class HttpConnectionEvent {

	private HttpServerConnection _httpConn;
	private final long _id;
	private long _writeTimestampMillis;
	private long _readBeginTimestampMillis;
	private long _readEndTimestampMillis;
	
	public static final EventFactory<HttpConnectionEvent> EVENT_FACTORY = new EventFactory<HttpConnectionEvent>() {
		private AtomicLong _idGenerator = new AtomicLong();

		@Override
		public HttpConnectionEvent newInstance() {
			return new HttpConnectionEvent(_idGenerator.getAndIncrement());
		}

	};

	public HttpConnectionEvent(final long id) {
		_id = id;
	}

	public void reset(){
		_httpConn = null;
		_writeTimestampMillis = 0;
		_readBeginTimestampMillis = 0;
		_readEndTimestampMillis = 0;
	}
	
	public HttpServerConnection get_httpConn() {
		return _httpConn;
	}

	public void set_httpConn(final HttpServerConnection _httpConn) {
		this._httpConn = _httpConn;
	}

	public long getId() {
		return _id;
	}

	public long getWriteTimestampMillis() {
		return _writeTimestampMillis;
	}

	public void setWriteTimestampMillis(long _writeTimestampMillis) {
		this._writeTimestampMillis = _writeTimestampMillis;
	}

	public long getReadBeginTimestampMillis() {
		return _readBeginTimestampMillis;
	}

	public void setReadBeginTimestampMillis(long _readBeginTimestampMillis) {
		this._readBeginTimestampMillis = _readBeginTimestampMillis;
	}

	public long getReadEndTimestampMillis() {
		return _readEndTimestampMillis;
	}

	public void setReadEndTimestampMillis(long _readEndTimestampMillis) {
		this._readEndTimestampMillis = _readEndTimestampMillis;
	}
}
