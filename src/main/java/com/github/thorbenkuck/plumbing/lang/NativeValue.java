package com.github.thorbenkuck.plumbing.lang;

final class NativeValue<T> implements Value<T> {

	private T value;

	NativeValue(T t) {
		value = t;
	}

	@Override
	public final void set(T t) {
		if (t == null) {
			throw new IllegalArgumentException("Null is not allowed as an Argument");
		}

		value = t;
	}

	@Override
	public final void clear() {
		value = null;
	}

	@Override
	public final T get() {
		return value;
	}
}
