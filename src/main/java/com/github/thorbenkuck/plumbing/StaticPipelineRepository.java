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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

final class StaticPipelineRepository {

	private static final AtomicBoolean doneLoading = new AtomicBoolean(false);
	private static InputStream inputStream;
	private static Map<Class<?>, Object> typeCache = new HashMap<>();
	private static Map<Class<?>, Pipeline<?>> pipelineMapping = new HashMap<>();
	private static ObjectFactory factory = new DefaultObjectFactory();

	static {
		inputStream = StaticPipelineRepository.class.getClassLoader().getResourceAsStream("pipelines.xml");
	}

	private StaticPipelineRepository() {
		throw new UnsupportedOperationException("Instantiation");
	}

	private static Object instantiate(Class<?> type) throws FactoryException {
		if (factory != null) {
			Object object;
			try {
				object = factory.create(type);
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

	private static void handle(PipelineInformation pipelineInformation) {
		Pipeline<?> pipeline = Pipeline.open();
		for (Class<?> handlerType : pipelineInformation.getHandlerTypes()) {
			if (!Runnable.class.isAssignableFrom(handlerType) && !Consumer.class.isAssignableFrom(handlerType) && !Function.class.isAssignableFrom(handlerType)) {
				throw new ParsingException("Only Runnable, Consumer and Function implementations can be added to a Pipeline. Given: " + handlerType);
			}

			Object instance;
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
		pipelineMapping.put(pipelineInformation.getType(), pipeline);
	}

	private static void handle(XMLUtils.IterableNodeList nodes) throws ParsingException {
		PipelineXMLParser pipelineXMLParser = new PipelineXMLParser(nodes);
		while (pipelineXMLParser.next()) {
			PipelineInformation pipelineInformation = pipelineXMLParser.getPipeline();
			handle(pipelineInformation);
		}
		typeCache.clear();
	}

	static void setXMLTarget(InputStream inputStream) {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		StaticPipelineRepository.inputStream = inputStream;

		if (doneLoading.get()) {
			doneLoading.set(false);
		}
	}

	static <T> Pipeline<T> find(Class<T> type) {
		return (Pipeline<T>) pipelineMapping.get(type);
	}

	static void tryLoadXML() throws ParsingException {
		if (doneLoading.get()) {
			return;
		}
		if (inputStream == null) {
			throw new ParsingException("Could not locate the InputStream to the XML-File");
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(inputStream);

			Element element = document.getDocumentElement();
			XMLUtils.IterableNodeList nodes = XMLUtils.walkable(element.getChildNodes());
			if (XMLUtils.isRoot(element)) {
				handle(nodes);
			} else {
				for (Node node : nodes) {
					if (XMLUtils.isRoot(node)) {
						handle(XMLUtils.walkable(node.getChildNodes()));
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ParsingException(e);
		}

		doneLoading.set(true);
	}

	static void setObjectFactory(ObjectFactory objectFactory) {
		StaticPipelineRepository.factory = objectFactory;
	}

	private static final class DefaultObjectFactory implements ObjectFactory {

		@Override
		public <T> T create(Class<T> type) throws FactoryException {
			Constructor<T> constructor;
			try {
				constructor = type.getConstructor();
			} catch (NoSuchMethodException e) {
				throw new FactoryException(e);
			}

			boolean accessible = true;
			T value;
			try {
				if (!constructor.isAccessible()) {
					constructor.setAccessible(true);
					accessible = false;
				}
				value = constructor.newInstance();
			} catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
				throw new FactoryException(e);
			} finally {
				if (!accessible) {
					constructor.setAccessible(false);
				}
			}

			return value;
		}
	}

}
