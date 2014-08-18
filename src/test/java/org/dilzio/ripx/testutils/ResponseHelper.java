package org.dilzio.ripx.testutils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dilzio
 * Date: 11/27/13
 */
public final class ResponseHelper {
    private ResponseHelper(){};

    public static String getBody(final HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity).trim();
    }


}
