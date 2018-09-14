package com.github.thorbenkuck.plumbing.pipeline;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Pipeline<T> {

	static <T> Pipeline<T> open() {
		return new NativePipeline<>();
	}

	void add(final Runnable runnable);

	void add(final Consumer<T> consumer);

	void add(final Function<T, T> function);

	void addFirst(final Runnable runnable);

	void addFirst(final Consumer<T> consumer);

	void addFirst(final Function<T, T> function);

	void addLast(final Runnable runnable);

	void addLast(final Consumer<T> consumer);

	void addLast(final Function<T, T> function);

	T apply(final T input);

	void breakConnections();

	void clear();

	boolean isEmpty();

	PipelineConnection<T> input();

	PipelineConnection<T> output();

	default void onConnect(final Pipeline<T> connected) {
	}

	default void onBrokenPipe(final Pipeline<T> connected) {
	}
}
