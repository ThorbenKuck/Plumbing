package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.lang.Value;

class PipelineInputConnection<T> implements PipelineConnection<T> {

	private final Pipeline<T> root;
	private final Value<Pipeline<T>> connected = Value.empty();

	PipelineInputConnection(Pipeline<T> root) {
		this.root = root;
	}

	@Override
	public void add(Pipeline<T> pipeline) {
		if (pipeline == root) {
			return;
		}
		pipeline.output().add(root);
		connected.set(pipeline);
	}

	@Override
	public void remove(Pipeline<T> pipeline) {
		breakUp();
	}

	@Override
	public void transfer(T t) {
		root.apply(t);
	}

	@Override
	public void transfer(Value<T> tValue) {
		tValue.set(root.apply(tValue.get()));
	}

	@Override
	public void breakUp() {
		Pipeline<T> old = connected.get();
		connected.clear();
		if (old != null) {
			old.output().remove(root);
			old.onBrokenPipe(root);
			root.onBrokenPipe(old);
			connected.clear();
		}
	}

	@Override
	public boolean isEmpty() {
		return connected.isEmpty();
	}
}
