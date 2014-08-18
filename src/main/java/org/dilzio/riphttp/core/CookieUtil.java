package org.dilzio.riphttp.core;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;


public class CookieUtil {
    private final DateTimeFormatter _fmt = DateTimeFormat.forPattern("E, dd MMM YYYY hh:mm:ss");

	public Map<String, Cookie> getCookiesFromRequest(final HttpRequest request) {
		Header[] headers = request.getAllHeaders();
		Map<String, Cookie> cookieMap = null;
		for (Header header : headers) {
			if (header.getName().equals("Cookie")) {
				if (null == cookieMap) {
					cookieMap = new HashMap<>(); //NOPMD
				}
				String[] cookieStrings = header.getValue().trim().split(";");
				for (String cookieString : cookieStrings) {
					String[] nameValPair = cookieString.trim().split("=");
					Cookie cookie = new BasicClientCookie(nameValPair[0], nameValPair[1]); //NOPMD
					cookieMap.put(cookie.getName(), cookie);
				}
			}
		}
		return cookieMap;
	}

	public BasicHeader makeCookieHeader(final String name, final String value, final String domain, final String path, final DateTime expiration, final boolean httpOnly, final boolean secure) {
		if (null == name || null == value) {
			throw new IllegalArgumentException("bad val: name: " + name + " value: " + value);
		}
		StringBuilder bldr = new StringBuilder(55); //arbitrary number greater than min chars that could be appended
		bldr.append(name + "=" + value + "; ");

		if (null != domain) {
			bldr.append("Domain=" + domain + "; ");
		}

        if (null != path) {
            bldr.append("Path=" + path + "; ");
        }

		if (null != expiration) {
			bldr.append("Expires=" + _fmt.print(expiration.toDateTime(DateTimeZone.UTC)) + " GMT; ");
		}

		if (httpOnly) {
			bldr.append("HttpOnly; ");
		}

		if (secure) {
			bldr.append("Secure; ");
		}
		return new BasicHeader("Set-Cookie", bldr.toString().trim());
	}

    public boolean checkCookiePresent(final HttpRequest request, final String uuidCookieName) {
        Map<String, Cookie> cookieMap = getCookiesFromRequest(request);
        if (null == cookieMap){
            return false;
        }
        return cookieMap.containsKey(uuidCookieName);
    }

    public Cookie getCookie(final HttpRequest request, final String uuidCookieName){
        Map<String, Cookie> cookieMap = getCookiesFromRequest(request);
        if (null == cookieMap){
            return null;
        }
        return cookieMap.get(uuidCookieName);
    }
}
