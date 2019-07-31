package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.TypeConverter;

final class InvertedTypeConverter<R, S> implements TypeConverter<R, S> {

	private final TypeConverter<S, R> root;

	InvertedTypeConverter(final TypeConverter<S, R> root) {
		this.root = root;
	}

	@Override
	public final S convert(R t) {
		return root.invert(t);
	}

	@Override
	public final R invert(S s) {
		return root.convert(s);
	}
}