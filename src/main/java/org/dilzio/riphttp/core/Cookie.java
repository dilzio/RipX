package org.dilzio.riphttp.core;

public class Cookie {
	private final String _name;
	private final String _value;

	public Cookie(final String name, final String value) {
		_name = name;
		_value = value;
	}

	public String getName() {
		return _name;
	}

	public String getValue() {
		return _value;
	}
}
