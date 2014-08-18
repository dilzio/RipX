package org.dilzio.riphttp.handlers;

import org.apache.http.*; //NOPMD
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Locale;

public class HttpFileHandler implements HttpRequestHandler {
	private static final Logger LOG = LoggerFactory.getLogger(HttpFileHandler.class.getName());
	private final String docRoot;

	public HttpFileHandler(final String docRoot) {
		super();
		this.docRoot = docRoot;
	}

	public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {

		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		if (!"GET".equals(method) && !"HEAD".equals(method) && !"POST".equals(method)) {
			throw new MethodNotSupportedException(method + " method not supported");
		}
		String target = request.getRequestLine().getUri();

		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			byte[] entityContent = EntityUtils.toByteArray(entity);
            LOG.debug("Incoming entity content (bytes): " + entityContent.length);
		}
		final File file = new File(this.docRoot, URLDecoder.decode(target, "UTF-8"));

		if (!file.exists()) { // NOPMD by dilzio on 10/10/13 8:30 PM
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			StringEntity entity = new StringEntity("<html><body><h1>File" + file.getPath() + " not found</h1></body></html>", ContentType.create("text/html", "UTF-8"));
			response.setEntity(entity);
			LOG.debug("File " + file.getPath() + " not found.");

		} else if (!file.canRead() || file.isDirectory()) {
			response.setStatusCode(HttpStatus.SC_FORBIDDEN);
			StringEntity entity = new StringEntity("<html><body><h1>Access denied</h1></body></html>", ContentType.create("text/html", "UTF-8"));
			response.setEntity(entity);
			LOG.debug("Cannot read file " +  file.getPath());
		} else {
			response.setStatusCode(HttpStatus.SC_OK);
			FileEntity body = new FileEntity(file, ContentType.create("text/html", (Charset) null));
			response.setEntity(body);
			LOG.debug("Serving file " +  file.getPath());
		}
	}
}