package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.TypeConverter;

import java.util.Objects;
import java.util.function.Function;

final class TypeConverterWrapper<T, S> implements Function<T, T> {

	private final Pipeline<S> core;
	private final TypeConverter<T, S> converter;

	TypeConverterWrapper(Pipeline<S> pipeline, TypeConverter<T, S> converter) {
		this.core = pipeline;
		this.converter = converter;
	}

	@Override
	public T apply(T t) {
		S converted = converter.convert(t);
		converted = core.apply(converted);
		return converter.invert(converted);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TypeConverterWrapper<?, ?> that = (TypeConverterWrapper<?, ?>) o;
		return Objects.equals(core, that.core);
	}

	@Override
	public int hashCode() {
		return Objects.hash(core);
	}

	void disconnectInput(Pipeline pipeline) {
		core.input().breakUp();
		pipeline.onBrokenPipe(core);
		core.onBrokenPipe(pipeline);
	}
}
