package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.TypeConverter;
import com.github.thorbenkuck.plumbing.lang.Value;

import java.util.function.Function;

final class PipelineInputConnection<T> implements PipelineConnection<T> {

	private final Pipeline<T> root;
	private final Value<Function<T, T>> connected = Value.empty();

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
	public <S> void add(Pipeline<S> pipeline, TypeConverter<T, S> converter) {
		if (pipeline == root) {
			return;
		}
		TypeConverterWrapper<T, S> wrapper = new TypeConverterWrapper<>(pipeline, converter);
		connected.set(wrapper);
		root.onConnect(pipeline);
		pipeline.output().add(root, new InvertedTypeConverter<>(converter));
		pipeline.onConnect(root);
	}

	@Override
	public final void remove(final Function<T, T> pipeline) {
		if (!connected.isEmpty() && connected.get().equals(pipeline)) {
			breakUp();
		}
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
		final Function<T, T> old = connected.get();
		connected.clear();
		if (old != null) {
			if (old instanceof Pipeline) {
				Pipeline<T> pipeline = (Pipeline<T>) old;
				pipeline.output().remove(root);
				pipeline.onBrokenPipe(root);
				root.onBrokenPipe(pipeline);
				connected.clear();
			}
		}
	}

	@Override
	public final boolean isEmpty() {
		return connected.isEmpty();
	}

	@Override
	public String toString() {
		return "PipelineInputConnection{" +
				"root=" + root +
				", connected=" + connected +
				'}';
	}
}
