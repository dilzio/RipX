package org.dilzio.riphttp.util;

public class Pair<T1, T2> {
	private T1 _first;
	private T2 _second;

	public void setFirst(final T1 value) {
		notNull(value);
		_first = value; 
		
	}

	public void setSecond(final T2 value) {
		notNull(value);
		_second = value;
	}

	public T1 first() {
		return _first;
	}

	public T2 second() {
		return _second;
	}

	private void notNull(Object value) {
		if (null == value){
			throw new IllegalArgumentException("Argument can't be null");
		}
	}
}
