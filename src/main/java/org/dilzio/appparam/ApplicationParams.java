package org.dilzio.appparam;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for managing application parameters in a strongly typed way. T should be a subclass of java.lang.Enum (i.e., a
 * regular java Enum) which implements the DefaultingEnum interface
 **/
public class ApplicationParams<T extends DefaultingEnum> {
	private final Map<T, String> _paramMap = new HashMap<>();

	public int getIntParam(final T paramName) {
		if (_paramMap.containsKey(paramName)) {
            return Integer.parseInt(_paramMap.get(paramName));
		}
		return Integer.parseInt(paramName.getDefault());
	}

	public boolean getBoolParam(final T paramName) {
		if (_paramMap.containsKey(paramName)) {
            return Boolean.parseBoolean(_paramMap.get(paramName));
		}
		return Boolean.parseBoolean(paramName.getDefault());
	}

	public float getFloatParam(final T paramName) {
		if (_paramMap.containsKey(paramName)) {
			return Float.parseFloat(_paramMap.get(paramName));
		}
		return Float.parseFloat(paramName.getDefault());
	}

	public String getStringParam(final T paramName) {
		if (_paramMap.containsKey(paramName)) {
			return _paramMap.get(paramName);
		}
		return paramName.getDefault();
	}

	public void setParam(final T paramName, final String paramVal) {
		_paramMap.put(paramName, paramVal);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (T pe : _paramMap.keySet()) {
			sb.append(pe + "=" + _paramMap.get(pe) + "\n");
		}
		if (sb.length() > 0) {
			return sb.toString();
		}

		return "none configured";
	}
}
