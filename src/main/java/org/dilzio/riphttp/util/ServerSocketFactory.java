package org.dilzio.riphttp.util;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Provides a server socket to a client
 * 
 * @author dilzio
 * 
 */
public interface ServerSocketFactory {
	ServerSocket getServerSocket(final int port) throws IOException;
}
