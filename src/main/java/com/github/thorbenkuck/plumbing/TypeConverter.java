package com.github.thorbenkuck.plumbing;

public interface TypeConverter<T, S> {

	S convert(final T t);

}
