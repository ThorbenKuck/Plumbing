package com.github.thorbenkuck.plumbing;

import com.github.thorbenkuck.plumbing.pipeline.Pipeline;

import java.util.List;

final class PipelineInformation {

	private final Class<?> type;
	private final List<Class<?>> handlerTypes;
	private final String name;
	private final List<OutputConnection> outputs;
	private Pipeline<?> pipeline;

	PipelineInformation(final Class<?> type, final List<Class<?>> handlerTypes, String name, List<OutputConnection> outputs) {
		this.type = type;
		this.handlerTypes = handlerTypes;
		this.name = name;
		this.outputs = outputs;
	}

	public String getName() {
		return name;
	}

	public List<OutputConnection> getOutputs() {
		return outputs;
	}

	public Pipeline<?> getPipeline() {
		return pipeline;
	}

	public void setPipeline(Pipeline<?> pipeline) {
		this.pipeline = pipeline;
	}

	final Class<?> getType() {
		return type;
	}

	final List<Class<?>> getHandlerTypes() {
		return handlerTypes;
	}
}
