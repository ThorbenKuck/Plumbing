package com.github.thorbenkuck.plumbing;

import com.github.thorbenkuck.plumbing.pipeline.Pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

final class NativePipelineRepository implements PipelineRepository {

	private static Map<PipelineIdentifier, Pipeline<?>> pipelineMapping = new HashMap<>();
	private final AtomicBoolean initialized = new AtomicBoolean(false);
	private final PipelineParsing pipelineParsing = new PipelineParsing();
	private ObjectFactory objectFactory = new DefaultConstructObjectFactory();

	private void apply(final Map<PipelineIdentifier, Pipeline<?>> mapping) {
		pipelineMapping.putAll(mapping);
	}

	private void tryAddDefaultTarget(final PipelineParsing pipelineParsing) {
		final InputStream inputStream = PipelineRepository.class.getClassLoader().getResourceAsStream("pipelines.xml");
		if (inputStream != null) {
			pipelineParsing.addTarget(inputStream);
		}
	}

	private <T> Pipeline<T> findAny(Class<T> type) {
		for (Map.Entry<PipelineIdentifier, Pipeline<?>> entry : pipelineMapping.entrySet()) {
			if (type.equals(entry.getKey().getType())) {
				return (Pipeline<T>) entry.getValue();
			}
		}

		throw new NoPipelineSetException(type);
	}

	@Override
	public void setDependencyResolver(final ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	@Override
	public void addXMLTarget(final InputStream inputStream) {
		if (initialized.get()) {
			return;
		}

		pipelineParsing.addTarget(inputStream);
	}

	@Override
	public void addXMLTarget(final URL url) throws IOException {
		if (initialized.get()) {
			return;
		}

		addXMLTarget(url.openStream());
	}

	@Override
	public void load() {
		pipelineParsing.setObjectFactory(objectFactory);
		tryAddDefaultTarget(pipelineParsing);
		final Map<PipelineIdentifier, Pipeline<?>> mapping = pipelineParsing.parse();
		apply(mapping);
	}

	@Override
	public void load(final Collection<InputStream> inputStreamCollection) {
		final PipelineParsing pipelineParsing = new PipelineParsing();
		inputStreamCollection.forEach(pipelineParsing::addTarget);
		tryAddDefaultTarget(pipelineParsing);
		pipelineParsing.setObjectFactory(objectFactory);
		final Map<PipelineIdentifier, Pipeline<?>> mapping = pipelineParsing.parse();
		apply(mapping);
	}

	@Override
	public <T> Pipeline<T> accessPipeline(final Class<T> type) {
		PipelineIdentifier identifier = new PipelineIdentifier(type, "NO_NAME");
		final Pipeline<T> pipeline = (Pipeline<T>) pipelineMapping.get(identifier);
		if (pipeline == null) {
			return findAny(type);
		}

		return pipeline;
	}

	@Override
	public <T> Pipeline<T> accessPipeline(final Class<T> type, final String name) {
		PipelineIdentifier identifier = new PipelineIdentifier(type, name);
		final Pipeline<T> pipeline = (Pipeline<T>) pipelineMapping.get(identifier);
		if (pipeline == null) {
			throw new NoPipelineSetException(type);
		}

		return pipeline;
	}
}
