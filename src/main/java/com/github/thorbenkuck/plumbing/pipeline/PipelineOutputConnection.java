package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.lang.Value;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

final class PipelineOutputConnection<T> implements PipelineConnection<T> {

	private final Pipeline<T> root;
	private final Queue<Pipeline<T>> exitPoints = new LinkedList<>();

	PipelineOutputConnection(final Pipeline<T> root) {
		this.root = root;
	}

	@Override
	public final void add(Pipeline<T> pipeline) {
		if (pipeline == root) {
			return;
		}
		if (!exitPoints.contains(pipeline)) {
			exitPoints.add(pipeline);
			root.onConnect(pipeline);
			pipeline.input().add(root);
			pipeline.onConnect(root);
		}
	}

	@Override
	public final void remove(final Pipeline<T> pipeline) {
		if (exitPoints.remove(pipeline)) {
			pipeline.input().remove(root);
		}
	}

	@Override
	public final void transfer(T t) {
		final Queue<Pipeline<T>> copy = new LinkedList<>(exitPoints);
		while (copy.peek() != null) {
			copy.poll().apply(t);
		}
	}

	@Override
	public final void transfer(final Value<T> tValue) {
		final Queue<Pipeline<T>> copy = new LinkedList<>(exitPoints);
		while (copy.peek() != null) {
			T temp = copy.poll().apply(tValue.get());
			tValue.set(temp);
		}
	}

	@Override
	public final void breakUp() {
		final List<Pipeline<T>> copy = new ArrayList<>(exitPoints);
		for (final Pipeline<T> pipeline : copy) {
			remove(pipeline);
		}
	}

	public final boolean isEmpty() {
		return exitPoints.isEmpty();
	}
}
