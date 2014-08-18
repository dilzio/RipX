package org.dilzio.ripx.testutils;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.util.List;

/**
 * User: Matt C.
 * Date: 11/27/13
 *
 *
 */
public class HttpTestClient {
    private final CloseableHttpClient _httpClient;

    public HttpTestClient() {
        _httpClient = HttpClients.custom().setDefaultRequestConfig(getRequestConfig()).build();
    }

    public HttpTestClient(final String keystorePath, final String keystorePassword) throws RuntimeException {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream instream = new FileInputStream(new File(keystorePath));
            trustStore.load(instream, keystorePassword.toCharArray());
            instream.close();
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            _httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse doGet(final String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return _httpClient.execute(httpGet);
    }

    public HttpResponse doGet(final URI uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        return _httpClient.execute(httpGet);
    }

    public HttpResponse doGet(final URI uri, final ResponseHandler responseHandler) {
        HttpGet httpGet = new HttpGet(uri);
        try {
            return (HttpResponse) _httpClient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public HttpResponse doGet(final URI uri, final HttpContext context) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        return _httpClient.execute(httpGet, context);
    }

    private RequestConfig getRequestConfig(){
        return   RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(0)
                .setConnectionRequestTimeout(0)
                .setStaleConnectionCheckEnabled(false)
                .setCookieSpec(CookieSpecs.BEST_MATCH)
                .build();
    }


    public RedirectLocations getRedirectLocationsFromContext(final HttpContext ctx){
        return (RedirectLocations) ctx.getAttribute("http.protocol.redirect-locations");
    }

    public HttpRequest getRequestFromContext(final HttpContext ctx){
       return (HttpRequest) ctx.getAttribute("http.request");
    }

    public List<Cookie> getCookiesFromContext(final HttpContext ctx){
        CookieStore cs = (CookieStore) ctx.getAttribute("http.cookie-store");

        if (null == cs){
            return null;
        }

        return cs.getCookies();
    }

    public HttpResponse getResponseFromContext(final HttpContext ctx) {
        return (HttpResponse) ctx.getAttribute("http.response");
    }
}
