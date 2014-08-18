package org.dilzio.riphttp.util;

public class Pair<T, V> {
	private T _first;
	private V _second;

	public void setFirst(final T value) {
		notNull(value);
		_first = value; 
		
	}

	public void setSecond(final V value) {
		notNull(value);
		_second = value;
	}

	public T first() {
		return _first;
	}

	public V second() {
		return _second;
	}

	private void notNull(final Object value) {
		if (null == value){
			throw new IllegalArgumentException("Argument can't be null");
		}
	}
}
