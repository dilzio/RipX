package org.dilzio.riphttp.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dilzio.riphttp.util.ITimeService;

import com.lmax.disruptor.RingBuffer;

public class ListenerDaemon implements Runnable {
	private static final Logger LOG = LogManager.getFormatterLogger(ListenerDaemon.class.getName());
	private final HttpConnectionFactory<DefaultBHttpServerConnection> _connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
	private final int _port;
	private final RingBuffer<HttpConnectionEvent> _ringBuffer;
	private final AtomicBoolean _isShutdown = new AtomicBoolean(false);
	private final ITimeService _timeService;
	private final boolean _poison;
	private final String _name;
	private final ServerSocket _listenerSocket;
	private Thread _runThread = null;

	public ListenerDaemon(final String name, final int port, final RingBuffer<HttpConnectionEvent> ringBuffer, final ServerSocket listenerSocket, final boolean poison, final ITimeService timeService) {
		_port = port;
		_ringBuffer = ringBuffer;
		_poison = poison;
		_timeService = timeService;
		_name = name;
		_listenerSocket = listenerSocket;
	}

	@Override
	public void run() {
		 _runThread= Thread.currentThread();

		LOG.info("%s listening for incoming connections on port %s", _name,  _port);
		try {
			while (!Thread.interrupted()) {
				try {
					Socket connectionSocket = _listenerSocket.accept();
					HttpServerConnection httpConnection = _connFactory.createConnection(connectionSocket);
					long sequence = _ringBuffer.next();
					//TODO...something less hacky for testing
					if (_poison && sequence > 0 && (sequence % 8) == 0)  {
							throw new RuntimeException("Listener RTE");
					}
					HttpConnectionEvent event = _ringBuffer.get(sequence);
					event.set_httpConn(httpConnection);
					event.setWriteTimestampMillis(_timeService.microTime());
					_ringBuffer.publish(sequence);
					LOG.trace("%s published event %s", _name, event.getId());
				} catch (IOException e) {
					if (_isShutdown.get()) {
						return;
					}
					LOG.error("Unable to accept connection: %s", e.getMessage());
				}
			}
		} finally {
			try {
				_listenerSocket.close();
			} catch (IOException e) {
				LOG.info("Unable to close listener Socket");
				throw new RuntimeException(e);
			}
		}
		LOG.info("Listener thread %s exiting", _runThread.getName());
	}

	public void stop() {
		_isShutdown.set(true);
		_runThread.interrupt();
		try {
			if (null != _listenerSocket) {
				_listenerSocket.close();
			}
		} catch (IOException e) {
			LOG.warn("IOException thrown on listenerSocket close");
		}
	}

}
