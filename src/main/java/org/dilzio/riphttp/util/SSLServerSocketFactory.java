package org.dilzio.riphttp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyStore;

public class SSLServerSocketFactory implements ServerSocketFactory {

	private static final Logger LOG = LoggerFactory.getLogger(SSLServerSocketFactory.class.getName());
	private final javax.net.ssl.SSLServerSocketFactory _sslSocketFactory;

	public SSLServerSocketFactory(final String keystorePath, final String password) {
		FileInputStream keystoreStream = null;
		try {
			keystoreStream = new FileInputStream(keystorePath);
			KeyStore keystore = KeyStore.getInstance("jks");
			keystore.load(keystoreStream, password.toCharArray());
			KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmfactory.init(keystore, password.toCharArray());
			KeyManager[] keymanagers = kmfactory.getKeyManagers();
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(keymanagers, null, null);
			_sslSocketFactory = sslcontext.getServerSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException("Error creating SSL listener socket factory.", e); //NOPMD
		} finally {
			try {
				keystoreStream.close();
			} catch (Exception e) {
				LOG.warn("Exception thrown while trying to close Keystore Stream");
			}
		}

	}

	@Override
	public ServerSocket getServerSocket(final int port) throws IOException {
		return _sslSocketFactory.createServerSocket(port);
	}
}
