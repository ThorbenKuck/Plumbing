package com.github.thorbenkuck.plumbing.lang;

import java.util.function.Consumer;

public interface Readable<T> {

	T get();

	default void requireNotEmpty() {
		if (isEmpty()) {
			throw new IllegalStateException("The Value is not readable");
		}
	}

	default boolean isEmpty() {
		return get() == null;
	}

	default void ifEmpty(Runnable runnable) {
		if (isEmpty()) {
			runnable.run();
		}
	}

	default void ifNotEmpty(Consumer<T> consumer) {
		if (!isEmpty()) {
			consumer.accept(get());
		}
	}

}
