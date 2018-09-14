package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.lang.Value;

final class PipelineInputConnection<T> implements PipelineConnection<T> {

	private final Pipeline<T> root;
	private final Value<Pipeline<T>> connected = Value.empty();

	PipelineInputConnection(final Pipeline<T> root) {
		this.root = root;
	}

	@Override
	public final void add(final Pipeline<T> pipeline) {
		if (pipeline == root) {
			return;
		}
		pipeline.output().add(root);
		connected.set(pipeline);
	}

	@Override
	public final void remove(final Pipeline<T> pipeline) {
		breakUp();
	}

	@Override
	public final void transfer(final T t) {
		root.apply(t);
	}

	@Override
	public final void transfer(final Value<T> tValue) {
		tValue.set(root.apply(tValue.get()));
	}

	@Override
	public final void breakUp() {
		final Pipeline<T> old = connected.get();
		connected.clear();
		if (old != null) {
			old.output().remove(root);
			old.onBrokenPipe(root);
			root.onBrokenPipe(old);
			connected.clear();
		}
	}

	@Override
	public final boolean isEmpty() {
		return connected.isEmpty();
	}
}
