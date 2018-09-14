package com.github.thorbenkuck.plumbing;

import java.util.Objects;

final class PipelineIdentifier {

	private final Class<?> type;
	private final String name;

	PipelineIdentifier(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PipelineIdentifier that = (PipelineIdentifier) o;
		if (that.name == null || that.name.equals("NO_NAME")) {
			return Objects.equals(that.type, type);
		}
		return Objects.equals(type, that.type) &&
				Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, name);
	}

	@Override
	public String toString() {
		return "PipelineIdentifier{" +
				"type=" + type +
				", name='" + name + '\'' +
				'}';
	}
}
