package org.dilzio.riphttp.core;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.ParseException;
import org.apache.http.cookie.Cookie;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 02/05/14
 * Time: 15:37
 */
public class CookieUtilTest {


    @Test
    public void testMultipleCookies() {
        HttpRequest mockRequest = mock(HttpRequest.class);
        Header cookieHeader = new Header() {
            @Override
            public String getName() {
                return "Cookie";
            }

            @Override
            public String getValue() {
                return "mako_uid=145bbc8c3db-ac0000010d15bf; __utma=151290669.1188550259.1399014811.1399014811.1399014811.1";
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        };
        Header[] headers = new Header[] { cookieHeader };
        when(mockRequest.getAllHeaders()).thenReturn(headers);

        CookieUtil underTest = new CookieUtil();

        Map<String, Cookie> cookieMap = underTest.getCookiesFromRequest(mockRequest);

        assertEquals("Number of cookies parsed", 2, cookieMap.size());
        assertNotNull("Cookie 1 exists", cookieMap.containsKey("mako_uid"));
        assertNotNull("Cookie 2 exists", cookieMap.containsKey("__utma"));

    }

    @Test
    public void testGetCookieByName() {

        HttpRequest mockRequest = mock(HttpRequest.class);
        Header cookieHeader = new Header() {
            @Override
            public String getName() {
                return "Cookie";
            }

            @Override
            public String getValue() {
                return "__utma=151290669.1188550259.1399014811.1399014811.1399014811.1; mako_uid=145bbc8c3db-ac0000010d15bf";
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        };
        Header[] headers = new Header[] { cookieHeader };
        when(mockRequest.getAllHeaders()).thenReturn(headers);

        CookieUtil underTest = new CookieUtil();

        Cookie cookie = underTest.getCookie(mockRequest, "mako_uid");

        assertNotNull("Cookie returned", cookie);
        assertEquals("Cookie name", "mako_uid", cookie.getName());
        assertEquals("Cookie value", "145bbc8c3db-ac0000010d15bf", cookie.getValue());


    }

}
