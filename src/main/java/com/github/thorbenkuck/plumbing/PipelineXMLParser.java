package com.github.thorbenkuck.plumbing;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class PipelineXMLParser {

	private final XMLUtils.IterableNodeList iterableNodeList;
	private int pointer = 0;
	private PipelineInformation currentPipeline;
	private Node currentElement;

	PipelineXMLParser(final XMLUtils.IterableNodeList iterableNodeList) {
		this.iterableNodeList = iterableNodeList;
	}

	private boolean isPipelineDeclaration() {
		return "pipeline".equals(currentElement.getNodeName());
	}

	private void fetch() {
		currentElement = iterableNodeList.item(pointer);
	}

	private void construct() throws ParsingException {
		final String typeName = findType();
		Class<?> type;
		try {
			type = Class.forName(typeName);
		} catch (ClassNotFoundException e) {
			throw new ParsingException("The provided class is not a valid class!", e);
		}

		final List<Class<?>> handlers = getHandlers();

		final String name = getName();

		final List<OutputConnection> connections = getConnections();

		Collections.reverse(handlers);

		currentPipeline = new PipelineInformation(type, handlers, name, connections);
	}

	private List<OutputConnection> getConnections() {
		final NodeList nodeList = currentElement.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if ("outputs".equals(node.getNodeName())) {
				return parseOutputs(node.getChildNodes());
			}
		}

		return Collections.emptyList();
	}

	private List<OutputConnection> parseOutputs(NodeList handlerList) {
		final List<OutputConnection> returnValue = new ArrayList<>();
		for (int i = 0; i < handlerList.getLength(); i++) {
			final Node current = handlerList.item(i);

			if ("output".equals(current.getNodeName())) {
				String name = "";
				Class<?> converter = null;
				NodeList outputs = current.getChildNodes();
				for (int j = 0; j < outputs.getLength(); j++) {
					Node inner = outputs.item(j);
					if ("name".equals(inner.getNodeName())) {
						name = inner.getTextContent();
					}
					if ("typeConverter".equals(inner.getNodeName())) {
						try {
							converter = Class.forName(inner.getTextContent());
						} catch (ClassNotFoundException e) {
							throw new ParsingException(e);
						}
					}
				}

				returnValue.add(new OutputConnection(name, converter));
			}
		}

		return returnValue;
	}

	private String getName() {
		if (currentElement.getAttributes().getNamedItem("name") != null) {
			return currentElement.getAttributes().getNamedItem("name").getNodeValue();
		}

		final NodeList nodeList = currentElement.getChildNodes();
		String type = "NO_NAME";
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node temp = nodeList.item(i);
			if ("name".equals(temp.getNodeName())) {
				type = temp.getTextContent();
				break;
			}
		}

		return type;
	}

	private List<Class<?>> parseIndividualHandlers(final NodeList handlerList) throws ParsingException {
		final List<Class<?>> returnValue = new ArrayList<>();
		for (int i = 0; i < handlerList.getLength(); i++) {
			final Node current = handlerList.item(i);
			if ("handler".equals(current.getNodeName())) {
				final String beanName;
				if (current.getAttributes().getNamedItem("bean") != null) {
					beanName = current.getAttributes().getNamedItem("bean").getNodeValue();
				} else {
					if (current.getTextContent() != null) {
						beanName = current.getTextContent();
					} else {
						throw new ParsingException("Could not find the bean");
					}
				}

				try {
					final Class<?> type = Class.forName(beanName);
					returnValue.add(type);
				} catch (final ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		return returnValue;
	}

	private List<Class<?>> getHandlers() throws ParsingException {
		final NodeList nodeList = currentElement.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			if ("handlers".equals(node.getNodeName())) {
				return parseIndividualHandlers(node.getChildNodes());
			}
		}

		return Collections.emptyList();
	}

	private String findType() throws ParsingException {
		if (currentElement.getAttributes().getNamedItem("type") != null) {
			return currentElement.getAttributes().getNamedItem("type").getNodeValue();
		}
		final NodeList nodeList = currentElement.getChildNodes();
		String type = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node temp = nodeList.item(i);
			if ("type".equals(temp.getNodeName())) {
				type = temp.getTextContent();
				break;
			}
		}

		if (type == null) {
			throw new ParsingException("Could not find the type");
		}
		return type;
	}

	final boolean next() throws ParsingException {
		if (pointer + 1 < iterableNodeList.getLength()) {
			++pointer;
			fetch();
			if (!isPipelineDeclaration()) {
				return next();
			} else {
				construct();
				return true;
			}
		}
		return false;
	}

	final PipelineInformation getPipeline() {
		return currentPipeline;
	}
}
