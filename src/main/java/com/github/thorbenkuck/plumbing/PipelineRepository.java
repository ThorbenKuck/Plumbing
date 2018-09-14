package com.github.thorbenkuck.plumbing;

import com.github.thorbenkuck.plumbing.pipeline.Pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface PipelineRepository {

	static PipelineRepository open() {
		return new NativePipelineRepository();
	}

	static void setDependencyResolver(ObjectFactory objectFactory) {
		StaticPipelineRepository.setObjectFactory(objectFactory);
	}

	static void initialize() throws ParsingException {
		StaticPipelineRepository.tryLoadXML();
	}

	static void initialize(URL url) throws IOException {
		StaticPipelineRepository.setXMLTarget(url.openStream());
		initialize();
	}

	static void initialize(InputStream inputStream) {
		StaticPipelineRepository.setXMLTarget(inputStream);
		initialize();
	}

	<T> Pipeline<T> accessPipeline(Class<T> type);

}
