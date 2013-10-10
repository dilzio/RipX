package org.dilzio.riphttp.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHeader;

public class CookieUtil {
	public Map<String, Cookie> getCookiesFromRequest(final HttpRequest request) {
		Header[] headers = request.getAllHeaders();
		Map<String, Cookie> cookieMap = null;
		for (Header header : headers) {
			if (header.getName().equals("Cookie")) {
				if (null == cookieMap) {
					cookieMap = new HashMap<String, Cookie>();
				}
				String[] cookieStrings = header.getValue().split(";");
				for (String cookieString : cookieStrings) {
					String[] nameValPair = cookieString.split("=");
					Cookie cookie = new Cookie(nameValPair[0], nameValPair[1]);
					cookieMap.put(cookie.getName(), cookie);
				}
			}
		}
		return cookieMap;
	}

	public BasicHeader getCookieHeader(final String name, final String value, final String domain, final String expiration, final boolean httpOnly, final boolean secure) {
		if (null == name || null == value) {
			throw new IllegalArgumentException(String.format("bad val: name: %s value: %s", name, value));
		}
		StringBuilder bldr = new StringBuilder();
		bldr.append(String.format("%s=%s; ", name, value));

		if (null != domain) {
			bldr.append(String.format("Domain=%s; ", domain));
		}

		if (null != expiration) {
			// TODO fix hard coding
			bldr.append(String.format("Expires=%s; ", "Saturday, August 27, 2203 8:16:28 PM"));
		}

		if (httpOnly) {
			bldr.append("HttpOnly;");
		}

		if (secure) {
			bldr.append("Secure;");
		}
		return new BasicHeader("Set-Cookie", bldr.toString());
	}
}
