package com.github.thorbenkuck.plumbing;

import java.util.List;

class PipelineInformation {

	private final Class<?> type;
	private final List<Class<?>> handlerTypes;

	PipelineInformation(Class<?> type, List<Class<?>> handlerTypes) {
		this.type = type;
		this.handlerTypes = handlerTypes;
	}

	Class<?> getType() {
		return type;
	}

	List<Class<?>> getHandlerTypes() {
		return handlerTypes;
	}
}
