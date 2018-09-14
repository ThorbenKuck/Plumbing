package com.github.thorbenkuck.plumbing;

import com.github.thorbenkuck.plumbing.pipeline.Pipeline;

class NativePipelineRepository implements PipelineRepository {
	@Override
	public <T> Pipeline<T> accessPipeline(Class<T> type) {
		return StaticPipelineRepository.find(type);
	}
}
