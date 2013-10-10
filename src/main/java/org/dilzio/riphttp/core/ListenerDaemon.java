package org.dilzio.riphttp.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
	public ListenerDaemon(final int port, final RingBuffer<HttpConnectionEvent> ringBuffer) {
		_port = port;
		_ringBuffer = ringBuffer;
	}

	@Override
	public void run() {
		LOG.info("Listening for incoming connections on port %s", _port);
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(_port);
		} catch (IOException e) {
			LOG.fatal("Unable to open listener socket on port %s. Aborting startup.", _port);
			System.exit(-1);
		}
		while(!Thread.interrupted()){
			try {
				Socket connectionSocket = listenerSocket.accept();
				HttpServerConnection httpConnection = _connFactory.createConnection(connectionSocket);
				long sequence = _ringBuffer.next();
				_ringBuffer.get(sequence).set_httpConn(httpConnection);
			    _ringBuffer.publish(sequence);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error("Unable to accept connection");
				e.printStackTrace();
			}
		}

		//Thread interrupt detected -- shut down
	    try {
			listenerSocket.close();
		} catch (IOException e) {
			LOG.error("Error on closing listener socket: %s", e.getMessage());
		}
	}

	public void stop(){
		Thread.currentThread().interrupt();
	}

}
