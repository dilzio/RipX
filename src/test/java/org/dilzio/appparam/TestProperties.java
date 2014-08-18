package org.dilzio.appparam;

/**
 * User: Matt C.
 * Date: 3/19/14
 *
 */
public enum TestProperties implements DefaultingEnum{
    INT_PROP("1234"),
    BOOL_PROP("false"),
    STRING_PROP("hi"),
    FLOAT_PROP("2.54");

    private final String _defaultVal;

    TestProperties(final String defaultVal){
        _defaultVal = defaultVal;
    }

    @Override
    public String getDefault() {
        return _defaultVal;
    }
}
