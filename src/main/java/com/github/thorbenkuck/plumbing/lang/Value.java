package com.github.thorbenkuck.plumbing.lang;

import java.util.function.Supplier;

public interface Value<T> extends Readable<T> {

	static <T> Value<T> of(final T t) {
		if (t == null) {
			throw new IllegalArgumentException("null");
		}
		return new NativeValue<>(t);
	}

	static <T> Value<T> synchronize(final T t) {
		if (t == null) {
			throw new IllegalArgumentException("null");
		}
		return new NativeSynchronizedValue<>(t);
	}

	static <T> Value<T> empty() {
		return new NativeValue<>(null);
	}

	static <T> Value<T> emptySynchronized() {
		return new NativeSynchronizedValue<>(null);
	}

	void set(final T t);

	void clear();

	default Readable<T> readOnly() {
		return this;
	}

	default void ifEmpty(final Supplier<T> supplier) {
		if (isEmpty()) {
			set(supplier.get());
		}
	}

	default void setIfEmpty(final T t) {
		if (isEmpty()) {
			set(t);
		}
	}

}
