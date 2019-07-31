package com.github.thorbenkuck.plumbing.provided;

import com.github.thorbenkuck.plumbing.TypeConverter;

public final class StringToIntTypeConverter implements TypeConverter<String, Integer> {
	@Override
	public final Integer convert(String s) {
		return Integer.valueOf(s);
	}

	@Override
	public final String invert(Integer integer) {
		return String.valueOf(integer);
	}
}
