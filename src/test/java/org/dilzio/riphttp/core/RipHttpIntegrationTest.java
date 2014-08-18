package org.dilzio.riphttp.core;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.dilzio.appparam.ApplicationParams;
import org.dilzio.riphttp.util.HttpMethod;
import org.dilzio.ripx.RipXProperties;
import org.dilzio.ripx.testutils.BasicTextResponseHandler;
import org.dilzio.ripx.testutils.HttpTestClient;
import org.dilzio.ripx.testutils.ResponseHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * In-container black-box tests for the RipHttp server.  All of these tests bring up a RipHttp server in varying configs and send calls via a Http test client.
 */
public class RipHttpIntegrationTest {

    public static final String HTTP_1_1_200_OK = "HTTP/1.1 200 OK";
    public static final String CALLED_FOO_HANDLER = "called /foo/* handler";
    public static final String CALLED_FOO_HANDLER1 = "called /foo handler";
    public static final String CALLED_HANDLER = "called * handler";

    @Test
    public void testStartStop() throws Exception {
        Route[] routes = {new Route("*", new HttpRequestHandler() {
            @Override
            public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

            }
        }, HttpMethod.GET)};

        RipHttp underTest = new RipHttp(getDefaultRipHttpParams(), getDefaultRipXParams(), routes);
        for (int i = 0; i < 4; i++) {
            underTest.start();
            underTest.stop();
        }
    }

    @Test
    public void testRouteUrlsAreCorrect() throws Exception {
        RipHttp underTest = null;
        try {

            Route[] routeList = {new Route("/foo/*", new BasicTextResponseHandler(CALLED_FOO_HANDLER), HttpMethod.GET),
                    new Route("/foo", new BasicTextResponseHandler(CALLED_FOO_HANDLER1), HttpMethod.GET),
                    new Route("*", new BasicTextResponseHandler(CALLED_HANDLER), HttpMethod.GET)};

            underTest = new RipHttp(getDefaultRipHttpParams(), getDefaultRipXParams(), routeList);

            underTest.start();
            HttpTestClient testClient = new HttpTestClient();
            HttpResponse response = testClient.doGet("http://localhost:8002/foo/");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals("called /foo/* handler", ResponseHelper.getBody(response));
            response = testClient.doGet("http://localhost:8002/foo/something");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals("called /foo/* handler", ResponseHelper.getBody(response));
            response = testClient.doGet("http://localhost:8002/foo");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals("called /foo handler", ResponseHelper.getBody(response));
            response = testClient.doGet("http://localhost:8002/foo");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals("called /foo handler", ResponseHelper.getBody(response));
            response = testClient.doGet("http://localhost:8002/foo?param1=blah&param2=moreblah");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals("called /foo handler", ResponseHelper.getBody(response));
            response = testClient.doGet("http://localhost:8002/bar");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals(CALLED_HANDLER, ResponseHelper.getBody(response));
            response = testClient.doGet("http://localhost:8002/bar/");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals(CALLED_HANDLER, ResponseHelper.getBody(response));
            response = testClient.doGet("http://localhost:8002/bar/foo");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals(CALLED_HANDLER, ResponseHelper.getBody(response));
            response = testClient.doGet("http://localhost:8002/bar?param=kaka");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals(CALLED_HANDLER, ResponseHelper.getBody(response));
        } finally {
            underTest.stop();
        }
    }

    @Test
    public void testHttps() throws Exception {
        RipHttp underTest = null;
        try {
            ApplicationParams params = getDefaultRipHttpParams();
            params.setParam(RipHttpProperties.USE_HTTPS, "true");
            params.setParam(RipHttpProperties.USE_HTTP, "true");
            params.setParam(RipHttpProperties.HTTPS_LISTEN_PORT, "8002");
            params.setParam(RipHttpProperties.HTTP_LISTEN_PORT, "8003");
            final String keystorePath = "./src/test/resources/org/dilzio/rippinhttp/testkeystore/testkeystore";
            final String keystorePswd = "password";
            params.setParam(RipHttpProperties.SSL_KEYSTORE, keystorePath);
            params.setParam(RipHttpProperties.SSL_KEYSTORE_PASSWORD, keystorePswd);


            Route[] routeList = {new Route("/foo/*", new BasicTextResponseHandler("called /foo/* handler"), HttpMethod.GET)};

            underTest = new RipHttp(params, getDefaultRipXParams(), routeList);

            underTest.start();
            HttpTestClient testClient = new HttpTestClient(keystorePath, keystorePswd);
            HttpResponse response = testClient.doGet("https://localhost:8002/foo/");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals("called /foo/* handler", ResponseHelper.getBody(response));

            //now test the that http works as well
            response = testClient.doGet("http://localhost:8003/foo/");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals("called /foo/* handler", ResponseHelper.getBody(response));

        } finally {
            underTest.stop();
        }
    }

    @Test
    @Ignore //right now client won't time out when server thread dies, so test hangs
    public void testRecoversFromCoredThreads() throws Exception {
        RipHttp underTest = null;
        try {
            ApplicationParams params = getDefaultRipHttpParams();
            params.setParam(RipHttpProperties.POISON_PILL, "true");
            params.setParam(RipHttpProperties.POISON_PILL_KILL_EVERY, "5");

            Route[] routeList = {new Route("/foo", new BasicTextResponseHandler("called /foo/* handler"), HttpMethod.GET)};

            underTest = new RipHttp(params, getDefaultRipXParams(), routeList);
            underTest.start();
            HttpTestClient testClient = new HttpTestClient();
            testClient.doGet("http://localhost:8002/foo");
            testClient.doGet("http://localhost:8002/foo");
            testClient.doGet("http://localhost:8002/foo");
            testClient.doGet("http://localhost:8002/foo");
            testClient.doGet("http://localhost:8002/foo");
            //Thread.sleep(5000);
            testClient.doGet("http://localhost:8002/foo");
        } finally {
            underTest.stop();
        }
    }

    @Test
    public void testCookieSetting() throws Exception {
        RipHttp underTest = null;
        try {
            Route[] routeList = {new Route("*", new HttpRequestHandler() {
                DateTime _expirationDate = new DateTime(2013, 11, 29, 00, 00, DateTimeZone.UTC).plusYears(1);

                @Override
                public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
                    BasicHeader header = new CookieUtil().makeCookieHeader("mycookie", "value1234", "eyeota.com", "/", _expirationDate, true, false);
                    BasicHeader header2 = new CookieUtil().makeCookieHeader("mycookie2", "abcd", "eyeota.com", "/", _expirationDate, false, true);
                    response.addHeader(header);
                    response.addHeader(header2);
                }
            }, HttpMethod.GET)};

            underTest = new RipHttp(getDefaultRipHttpParams(), getDefaultRipXParams(), routeList);

            underTest.start();
            HttpTestClient testClient = new HttpTestClient();
            HttpResponse response = testClient.doGet("http://localhost:8002/foo");
            Header[] cookieHeaders = response.getHeaders("Set-Cookie");
            assertEquals("Set-Cookie", cookieHeaders[0].getName());
            assertEquals("mycookie=value1234; Domain=eyeota.com; Path=/; Expires=Sat, 29 Nov 2014 12:00:00 GMT; HttpOnly;", cookieHeaders[0].getValue());
            assertEquals("Set-Cookie", cookieHeaders[1].getName());
            assertEquals("mycookie2=abcd; Domain=eyeota.com; Path=/; Expires=Sat, 29 Nov 2014 12:00:00 GMT; Secure;", cookieHeaders[1].getValue());
        } finally {
            underTest.stop();
        }
    }

    @Test
    public void simpleExample() throws Exception {
        RipHttp underTest = null;
        try {
            /**
             * Set config.  Can either pass in properties file path with all properties for RipHttp and Ripx
             * or can pass in two Application Params objects.
             */
            ApplicationParams<RipXProperties> ripXParams = new ApplicationParams();
            ripXParams.setParam(RipXProperties.SERVER_NAME, "test server");

            ApplicationParams<RipHttpProperties> ripHttpParams = new ApplicationParams();
            ripHttpParams.setParam(RipHttpProperties.HTTP_LISTEN_PORT, "8002");
            /**
             * Create an array of Routes.  A Route is a combination of an URL endpoint, an implementation of HttpRequestHandler
             * and 1 or more Http Methods.  There are some basic HttpRequestHandler implementations in the library for returning static content, but generally these
             * are implemented by you.
             *
             */
            Route[] routes =
            {
                    //Route 1 - handler as anon class
                    new Route("/foo/*", new HttpRequestHandler() {
                        @Override
                        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
                            response.setEntity(new StringEntity("<b>foo handler: a simple HTML response, this could be JSON, etc..</b>"));
                        }
                    }, HttpMethod.GET),

                    //Route 2 - handler defined somewhere else
                    new Route("/bar/*", new BasicTextResponseHandler("<b>bar handler</b>"), HttpMethod.GET)
            };

            /**
             * Instantiate server. Routes param is varargs if you prefer to pass individual routes.
             */
            underTest = new RipHttp(ripHttpParams, ripXParams, routes);
            /*
             * alternately can pass in a properties file containing name/val pairs for all properties for both riphttp and ripx
             * e.g.: underTest = new RipHttp("/path/to/properties.file", routes);
             */



            /**
             * Start the server up
             */
            underTest.start();

            /**
             * Use a test client to call the server and verify the contents.
             *
             */
            HttpTestClient testClient = new HttpTestClient();
            HttpResponse response = testClient.doGet("http://localhost:8002/foo/");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals("<b>foo handler: a simple HTML response, this could be JSON, etc..</b>", ResponseHelper.getBody(response));
            response = testClient.doGet("http://localhost:8002/bar/");
            assertEquals(HTTP_1_1_200_OK, response.getStatusLine().toString());
            assertEquals("<b>bar handler</b>", ResponseHelper.getBody(response));
        } finally {
            underTest.stop();
        }
    }

    private ApplicationParams<RipHttpProperties> getDefaultRipHttpParams() {
        ApplicationParams<RipHttpProperties> params = new ApplicationParams();
        params.setParam(RipHttpProperties.HTTP_LISTEN_PORT, "8002");
        params.setParam(RipHttpProperties.HTTPS_LISTEN_PORT, "8003");
        return params;
    }

    private ApplicationParams<RipXProperties> getDefaultRipXParams() {
        ApplicationParams<RipXProperties> params = new ApplicationParams();
        params.setParam(RipXProperties.SERVER_NAME, "test server");
        return params;
    }
}
