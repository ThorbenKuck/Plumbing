package com.github.thorbenkuck.plumbing;

import com.github.thorbenkuck.plumbing.pipeline.Pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

public interface PipelineRepository {

	static PipelineRepository open() {
		return new NativePipelineRepository();
	}

	void setDependencyResolver(final ObjectFactory objectFactory);

	void addXMLTarget(final InputStream inputStream);

	void addXMLTarget(final URL url) throws IOException;

	void load();

	void load(final Collection<InputStream> inputStreamCollection);

	<T> Pipeline<T> accessPipeline(final Class<T> type);

	<T> Pipeline<T> accessPipeline(Class<T> type, String name);
}
