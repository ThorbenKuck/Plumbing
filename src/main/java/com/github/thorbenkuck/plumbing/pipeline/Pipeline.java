package com.github.thorbenkuck.plumbing.pipeline;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Pipeline<T> {

	static <T> Pipeline<T> open() {
		return new NativePipeline<>();
	}

	void add(Runnable runnable);

	void add(Consumer<T> consumer);

	void add(Function<T, T> function);

	void addFirst(Runnable runnable);

	void addFirst(Consumer<T> consumer);

	void addFirst(Function<T, T> function);

	void addLast(Runnable runnable);

	void addLast(Consumer<T> consumer);

	void addLast(Function<T, T> function);

	T apply(T input);

	void breakConnections();

	void clear();

	boolean isEmpty();

	PipelineConnection<T> input();

	PipelineConnection<T> output();

	default void onConnect(Pipeline<T> connected) {
		System.out.println("Connected ..");
	}

	default void onBrokenPipe(Pipeline<T> connected) {
		System.out.println("Disconnected ..");
	}
}
