package org.dilzio.riphttp.core;

import com.lmax.disruptor.EventFactory;
import org.apache.http.HttpServerConnection;
import org.dilzio.ripx.AbstractRipXEvent;

import java.util.concurrent.atomic.AtomicLong;

public final class HttpConnectionEvent extends AbstractRipXEvent {

	private HttpServerConnection _httpConn;

	public static final EventFactory<HttpConnectionEvent> EVENT_FACTORY = new EventFactory<HttpConnectionEvent>() {
		private AtomicLong _idGenerator = new AtomicLong();

		@Override
		public HttpConnectionEvent newInstance() {
			return new HttpConnectionEvent(_idGenerator.getAndIncrement());
		}

	};

    public HttpConnectionEvent(final long id) {
        super(id);
    }

    @Override
    protected void resetExtendedFields() {
       _httpConn = null;
    }

	public HttpServerConnection getHttpConn() {
		return _httpConn;
	}

	public void setHttpConn(final HttpServerConnection _httpConn) {
		this._httpConn = _httpConn;
	}
}
