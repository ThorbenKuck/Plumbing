package com.github.thorbenkuck.plumbing;

import com.github.thorbenkuck.plumbing.exceptions.FactoryException;
import com.github.thorbenkuck.plumbing.pipeline.Pipeline;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

final class PipelineParsing {

	private final List<InputStream> targets = new ArrayList<>();
	private final Map<Class<?>, Object> typeCache = new HashMap<>();
	private ObjectFactory objectFactory;

	private Object instantiate(final Class<?> type) throws FactoryException {
		if (objectFactory != null) {
			final Object object;
			try {
				object = objectFactory.create(type);
			} catch (FactoryException e) {
				throw e;
			} catch (Exception e) {
				throw new FactoryException(e);
			}
			if (object != null) {
				return object;
			} else {
				throw new FactoryException("Could not instantiate the requested type " + type);
			}
		} else {
			throw new FactoryException("No ObjectFactory set!");
		}
	}

	private void create(final PipelineInformation pipelineInformation) {
		final Pipeline<?> pipeline = Pipeline.open();

		for (final Class<?> handlerType : pipelineInformation.getHandlerTypes()) {
			if (!Runnable.class.isAssignableFrom(handlerType) && !Consumer.class.isAssignableFrom(handlerType) && !Function.class.isAssignableFrom(handlerType)) {
				throw new ParsingException("Only Runnable, Consumer and Function implementations can be added to a Pipeline. Given: " + handlerType);
			}

			final Object instance;
			if (typeCache.get(handlerType) == null) {
				try {
					instance = instantiate(handlerType);
					typeCache.put(handlerType, instance);
				} catch (FactoryException e) {
					throw new ParsingException("Could not find, or instantiate the requested Handler: " + handlerType + " for the Pipeline of the type " + pipelineInformation.getType(), e);
				}
			} else {
				instance = typeCache.get(handlerType);
			}

			if (Runnable.class.isAssignableFrom(handlerType)) {
				pipeline.add((Runnable) instance);
			} else if (Consumer.class.isAssignableFrom(handlerType)) {
				pipeline.add((Consumer) instance);
			} else if (Function.class.isAssignableFrom(handlerType)) {
				pipeline.add((Function) instance);
			}
		}

		pipelineInformation.setPipeline(pipeline);
	}

	private List<PipelineInformation> handle(final XMLUtils.IterableNodeList nodes) throws ParsingException {
		final PipelineXMLParser pipelineXMLParser = new PipelineXMLParser(nodes);
		final List<PipelineInformation> returnValue = new ArrayList<>();
		while (pipelineXMLParser.next()) {
			final PipelineInformation pipelineInformation = pipelineXMLParser.getPipeline();
			create(pipelineInformation);
			returnValue.add(pipelineInformation);
		}
		typeCache.clear();

		return returnValue;
	}

	private List<PipelineInformation> load(final InputStream inputStream) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();

			final Document document = builder.parse(inputStream);

			final Element element = document.getDocumentElement();
			final XMLUtils.IterableNodeList nodes = XMLUtils.walkable(element.getChildNodes());
			if (XMLUtils.isRoot(element)) {
				return handle(nodes);
			} else {
				for (final Node node : nodes) {
					if (XMLUtils.isRoot(node)) {
						return handle(XMLUtils.walkable(node.getChildNodes()));
					}
				}
				throw new ParsingException("Could not find the <pipelines> root in the provided inputStream " + inputStream);
			}
		} catch (final ParserConfigurationException | SAXException | IOException e) {
			throw new ParsingException(e);
		}
	}

	private List<PipelineInformation> firstRound() {
		final List<PipelineInformation> returnValue = new ArrayList<>();

		for (final InputStream inputStream : targets) {
			returnValue.addAll(load(inputStream));
		}
		return returnValue;
	}

	private Map<PipelineIdentifier, Pipeline<?>> secondRound(List<PipelineInformation> informationList) {
		final Map<PipelineIdentifier, Pipeline<?>> mapping = new HashMap<>();
		final Map<String, PipelineInformation> mappedToName = new HashMap<>();

		for (PipelineInformation information : informationList) {
			if (!information.getName().equals("NO_NAME")) {
				mappedToName.put(information.getName(), information);
			}
		}

		for (PipelineInformation information : informationList) {
			for (OutputConnection connection : information.getOutputs()) {
				connect(information.getPipeline(), information, mappedToName.get(connection.getName()), connection);
			}
		}

		mappedToName.clear();

		for (PipelineInformation information : informationList) {
			mapping.put(new PipelineIdentifier(information.getType(), information.getName()), information.getPipeline());
		}

		return mapping;
	}

	private <T> void connect(Pipeline<T> root, PipelineInformation rootInformation, PipelineInformation toConnect, OutputConnection connection) {
		if (toConnect == null) {
			throw new ParsingException("Could not find the pipeline with the name " + connection.getName());
		}
		if (toConnect.getType().equals(rootInformation.getType())) {
			root.output().add((Pipeline<T>) toConnect.getPipeline());
		} else {
			throw new ParsingException("The two Pipelines are not compatible!" +
					"\nPipeline 1:" +
					"\nname=" + rootInformation.getName() + "" +
					"\ntype=" + rootInformation.getType() +
					"\nPipeline 2:" +
					"\nname=" + toConnect.getName() + "" +
					"\ntype=" + toConnect.getType());
		}

	}

	final void setObjectFactory(final ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	final void addTarget(final InputStream inputStream) {
		if (inputStream == null) {
			throw new NullPointerException();
		}
		targets.add(inputStream);
	}

	final Map<PipelineIdentifier, Pipeline<?>> parse() {
		final List<PipelineInformation> informationList = firstRound();

		return secondRound(informationList);
	}

}
