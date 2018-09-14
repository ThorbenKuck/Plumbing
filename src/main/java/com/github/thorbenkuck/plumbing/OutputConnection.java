package com.github.thorbenkuck.plumbing;

final class OutputConnection {

	private final String name;
	private Class<?> converter;

	OutputConnection(String name, Class<?> converter) {
		this.name = name;
		this.converter = converter;
	}

	public String getName() {
		return name;
	}

	public Class<?> getConverter() {
		return converter;
	}
}
