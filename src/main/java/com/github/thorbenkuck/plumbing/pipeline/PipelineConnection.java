package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.lang.Value;

public interface PipelineConnection<T> {

	static <T> PipelineConnection<T> input(Pipeline<T> root) {
		return new PipelineInputConnection<>(root);
	}

	static <T> PipelineConnection<T> output(Pipeline<T> root) {
		return new PipelineOutputConnection<>(root);
	}

	void add(Pipeline<T> pipeline);

	void remove(Pipeline<T> pipeline);

	void transfer(T t);

	void transfer(Value<T> tValue);

	void breakUp();

	boolean isEmpty();

}
