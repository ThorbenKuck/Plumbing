package com.github.thorbenkuck.plumbing.lang;

final class NativeValue<T> implements Value<T> {

	private T value;

	NativeValue(final T t) {
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

	@Override
	public String toString() {
		return "Value{" +
				(value == null ? "empty" : "value=" + value) +
				'}';
	}
}
