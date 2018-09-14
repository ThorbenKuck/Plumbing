package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.lang.Value;

public interface PipelineConnection<T> {

	static <T> PipelineConnection<T> input(final Pipeline<T> root) {
		return new PipelineInputConnection<>(root);
	}

	static <T> PipelineConnection<T> output(final Pipeline<T> root) {
		return new PipelineOutputConnection<>(root);
	}

	void add(final Pipeline<T> pipeline);

	void remove(final Pipeline<T> pipeline);

	void transfer(final T t);

	void transfer(final Value<T> tValue);

	void breakUp();

	boolean isEmpty();

}
