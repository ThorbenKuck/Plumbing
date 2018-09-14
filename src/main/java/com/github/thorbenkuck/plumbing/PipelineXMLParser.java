package com.github.thorbenkuck.plumbing;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PipelineXMLParser {

	private final XMLUtils.IterableNodeList iterableNodeList;
	private int pointer = 0;
	private PipelineInformation currentPipeline;
	private Node currentElement;

	PipelineXMLParser(XMLUtils.IterableNodeList iterableNodeList) {
		this.iterableNodeList = iterableNodeList;
	}

	private boolean isPipelineDeclaration() {
		return "pipeline".equals(currentElement.getNodeName());
	}

	private void fetch() {
		currentElement = iterableNodeList.item(pointer);
	}

	private void construct() throws ParsingException {
		String typeName = findType();
		Class<?> type;
		try {
			type = Class.forName(typeName);
		} catch (ClassNotFoundException e) {
			throw new ParsingException("The provided class is not a valid class!", e);
		}

		List<Class<?>> handlers = getHandlers();

		Collections.reverse(handlers);

		currentPipeline = new PipelineInformation(type, handlers);
	}

	private List<Class<?>> parseIndividualHandlers(NodeList handlerList) throws ParsingException {
		List<Class<?>> returnValue = new ArrayList<>();
		for (int i = 0; i < handlerList.getLength(); i++) {
			Node current = handlerList.item(i);
			if ("handler".equals(current.getNodeName())) {
				String beanName;
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
					Class<?> type = Class.forName(beanName);
					returnValue.add(type);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		return returnValue;
	}

	private List<Class<?>> getHandlers() throws ParsingException {
		NodeList nodeList = currentElement.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
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
		NodeList nodeList = currentElement.getChildNodes();
		String type = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node temp = nodeList.item(i);
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

	public boolean next() throws ParsingException {
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

	public PipelineInformation getPipeline() {
		return currentPipeline;
	}
}
