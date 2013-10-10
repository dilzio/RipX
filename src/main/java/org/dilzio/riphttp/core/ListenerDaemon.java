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

import com.lmax.disruptor.RingBuffer;

public class ListenerDaemon implements Runnable {
	private static final Logger LOG = LogManager.getFormatterLogger(ListenerDaemon.class.getName());
	private final HttpConnectionFactory<DefaultBHttpServerConnection> _connFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
	private final int _port;
	private final RingBuffer<HttpConnectionEvent> _ringBuffer;
	private final AtomicBoolean _isShutdown = new AtomicBoolean(false);
	
	private ServerSocket _listenerSocket = null;
	private Thread _runThread = null;
	
	public ListenerDaemon(final int port, final RingBuffer<HttpConnectionEvent> ringBuffer) {
		_port = port;
		_ringBuffer = ringBuffer;
	}

	@Override
	public void run() {
		_runThread = Thread.currentThread();
		LOG.info("Listening for incoming connections on port %s", _port);

		try {
			_listenerSocket = new ServerSocket(_port);
		} catch (IOException e) {
			LOG.fatal("Unable to open listener socket on port %s. Aborting startup.", _port);
			throw new RuntimeException(e);
		}
		while(!Thread.interrupted()){
			try {
				Socket connectionSocket = _listenerSocket.accept();
				HttpServerConnection httpConnection = _connFactory.createConnection(connectionSocket);
				long sequence = _ringBuffer.next();
				_ringBuffer.get(sequence).set_httpConn(httpConnection);
			    _ringBuffer.publish(sequence);
			} catch (IOException e) {
				if (_isShutdown.get()){
					return;
				}
				LOG.error("Unable to accept connection: %s", e);
			}
		}
		LOG.info("Listener thread %s exiting", _runThread.getName());
	}

	public void stop(){
		_isShutdown.set(true);
		_runThread.interrupt();
		try {
			if (null != _listenerSocket){
				_listenerSocket.close();
			}
		} catch (IOException e) {
			LOG.warn("IOException thrown on listenerSocket close");
		}
	}

}
