package com.github.thorbenkuck.plumbing.pipeline;

import java.util.function.Function;

public interface RawPipeline<T> extends Function<T, T> {

	PipelineConnection<T> input();

	PipelineConnection<T> output();

	default void onConnect(final Pipeline connected) {
	}

	default void onBrokenPipe(final Pipeline connected) {
	}

}
