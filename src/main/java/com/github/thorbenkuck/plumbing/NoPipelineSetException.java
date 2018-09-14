package com.github.thorbenkuck.plumbing;

public final class NoPipelineSetException extends RuntimeException {

	public NoPipelineSetException(Class<?> type) {
		super("There is no Pipeline set for the type " + type);
	}
}
