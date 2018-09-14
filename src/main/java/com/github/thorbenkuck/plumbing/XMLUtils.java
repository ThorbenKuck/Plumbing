package com.github.thorbenkuck.plumbing;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.function.Consumer;

final class XMLUtils {

	private static final String EXPECTED_ROOT = "pipelines";

	static boolean isRoot(final Element element) {
		return EXPECTED_ROOT.equals(element.getTagName());
	}

	static boolean isRoot(final Node node) {
		return EXPECTED_ROOT.equals(node.getNodeName());
	}

	static IterableNodeList walkable(final NodeList nodeList) {
		return new IterableNodeList(nodeList);
	}

	static final class IterableNodeList implements NodeList, Iterable<Node> {

		private final NodeList core;

		IterableNodeList(final NodeList core) {
			this.core = core;
		}

		public final void walk(final Consumer<Node> nodeConsumer) {
			for (final Node node : this) {
				nodeConsumer.accept(node);
			}
		}

		@Override
		public final Iterator<Node> iterator() {
			return new NodeListIterator(core);
		}

		@Override
		public final Node item(final int index) {
			return core.item(index);
		}

		@Override
		public final int getLength() {
			return core.getLength();
		}

		private final class NodeListIterator implements Iterator<Node> {

			private final NodeList nodeList;
			private int current = 0;

			private NodeListIterator(NodeList nodeList) {
				this.nodeList = nodeList;
			}

			@Override
			public boolean hasNext() {
				return current + 1 < nodeList.getLength();
			}

			@Override
			public Node next() {
				++current;
				return nodeList.item(current);
			}
		}
	}

}
