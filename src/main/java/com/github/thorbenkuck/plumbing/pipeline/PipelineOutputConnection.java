package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.TypeConverter;
import com.github.thorbenkuck.plumbing.lang.Value;

import java.util.*;
import java.util.function.Function;

final class PipelineOutputConnection<T> implements PipelineConnection<T> {

	private final Pipeline<T> root;
	private final Queue<Function<T, T>> exitPoints = new LinkedList<>();

	PipelineOutputConnection(final Pipeline<T> root) {
		this.root = root;
	}

	private void removeInternally(Function<T, T> function) {
		if (!exitPoints.remove(function)) {
			return;
		}
		if (function instanceof TypeConverterWrapper) {
			TypeConverterWrapper wrapper = (TypeConverterWrapper) function;
			wrapper.disconnectInput(root);
		} else if (function instanceof Pipeline) {
			((Pipeline<T>) function).input().remove(root);
		} else {
			throw new IllegalStateException("Found unsupported type within this Connection!");
		}
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
	public final <S> void add(Pipeline<S> pipeline, TypeConverter<T, S> converter) {
		if (pipeline == root) {
			return;
		}
		TypeConverterWrapper<T, S> wrapper = new TypeConverterWrapper<>(pipeline, converter);
		if (!exitPoints.contains(wrapper)) {
			exitPoints.add(wrapper);
			root.onConnect(pipeline);
			pipeline.input().add(root, new InvertedTypeConverter<>(converter));
			pipeline.onConnect(root);
		}
	}

	@Override
	public final void remove(final Function<T, T> pipeline) {
		removeInternally(pipeline);
	}

	@Override
	public final void transfer(T t) {
		final Queue<Function<T, T>> copy = new LinkedList<>(exitPoints);
		while (copy.peek() != null) {
			Objects.requireNonNull(copy.poll()).apply(t);
		}
	}

	@Override
	public final void transfer(final Value<T> tValue) {
		final Queue<Function<T, T>> copy = new LinkedList<>(exitPoints);
		while (copy.peek() != null) {
			T temp = Objects.requireNonNull(copy.poll()).apply(tValue.get());
			tValue.set(temp);
		}
	}

	@Override
	public final void breakUp() {
		final List<Function<T, T>> copy = new ArrayList<>(exitPoints);
		for (final Function<T, T> pipeline : copy) {
			removeInternally(pipeline);
		}
	}

	@Override
	public final boolean isEmpty() {
		return exitPoints.isEmpty();
	}

	@Override
	public String toString() {
		return "PipelineOutputConnection{" +
				"root=" + root +
				", exitPoints=" + exitPoints +
				'}';
	}
}
