package org.dilzio.riphttp.util;

import java.io.IOException;
import java.net.ServerSocket;

public class BasicServerSocketFactory implements ServerSocketFactory {
	@Override
	public ServerSocket getServerSocket(int port) throws IOException {
		return new ServerSocket(port);
	}
}
