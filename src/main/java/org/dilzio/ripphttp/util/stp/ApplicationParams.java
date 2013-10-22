package org.dilzio.ripphttp.util.stp;

import java.util.HashMap;
import java.util.Map;

import org.dilzio.riphttp.util.ParamEnum;

public class ApplicationParams {
	private final Map<ParamEnum, String> _paramMap = new HashMap<ParamEnum, String>();

	public int getIntParam(ParamEnum paramName) {
		if (_paramMap.containsKey(paramName)) {
			return Integer.valueOf(_paramMap.get(paramName)).intValue();
		}
		return Integer.valueOf(paramName.getDefault()).intValue();
	}

	public boolean getBoolParam(ParamEnum paramName) {
		if (_paramMap.containsKey(paramName)) {
			return Boolean.valueOf(_paramMap.get(paramName)).booleanValue();
		}
		return Boolean.valueOf(paramName.getDefault()).booleanValue();
	}

	public float getFloatParam(ParamEnum paramName) {
		if (_paramMap.containsKey(paramName)) {
			return Float.valueOf(_paramMap.get(paramName)).floatValue();
		}
		return Float.valueOf(paramName.getDefault()).floatValue();
	}

	public String getStringParam(ParamEnum paramName) {
		if (_paramMap.containsKey(paramName)) {
			return _paramMap.get(paramName);
		}
		return paramName.getDefault();
	}

	public void setParam(ParamEnum paramName, String paramVal) {
		_paramMap.put(paramName, paramVal);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ParamEnum pe : _paramMap.keySet()) {
			sb.append(pe + "=" + _paramMap.get(pe) + "\n");
		}
		if (sb.length() > 0) {
			return sb.toString();
		}

		return "none configured";
	}
}
