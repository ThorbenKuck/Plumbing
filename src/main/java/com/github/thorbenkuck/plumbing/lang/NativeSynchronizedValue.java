package com.github.thorbenkuck.plumbing.lang;

final class NativeSynchronizedValue<T> implements Value<T> {

	private T value;

	NativeSynchronizedValue(final T t) {
		value = t;
	}

	@Override
	public final synchronized void set(final T t) {
		if (t == null) {
			throw new IllegalArgumentException("Null is not allowed as an Argument");
		}

		value = t;
	}

	@Override
	public final synchronized void clear() {
		value = null;
	}

	@Override
	public final synchronized T get() {
		return value;
	}
}
