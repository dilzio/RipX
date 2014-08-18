package org.dilzio.riphttp.core;

import org.dilzio.appparam.DefaultingEnum;

/**
 * User: Matt C.
 * Date: 3/18/14
 *
 * Properties Enum for RipHttp
 */
public enum RipHttpProperties implements DefaultingEnum {
    HTTP_LISTEN_PORT("8081"),
    HTTPS_LISTEN_PORT("8082"),
    USE_HTTP("true"),      //open an http port
    USE_HTTPS("false"),          //open an https port
    SSL_KEYSTORE(null),
    SSL_KEYSTORE_PASSWORD(null),
    SERVER_NAME("RipHTTP"),
    SERVER_VERSION("1.0"),
    DOCROOT("."),
    POISON_PILL("false"),
    POISON_PILL_KILL_EVERY("50");

    private final String _default;

    RipHttpProperties(final String defaultValue){
        _default = defaultValue;
    }

    @Override
    public String getDefault() {
        return _default;
    }
}
