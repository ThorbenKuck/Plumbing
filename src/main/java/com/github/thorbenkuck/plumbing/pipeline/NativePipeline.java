package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.lang.Value;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

class NativePipeline<T> implements Pipeline<T> {

	private final Deque<Function<T, T>> core = new ArrayDeque<>();
	private final PipelineConnection<T> input = PipelineConnection.input(this);
	private final PipelineConnection<T> output = PipelineConnection.output(this);

	private Function<T, T> wrap(Consumer<T> consumer) {
		return new ConsumerWrapper(consumer);
	}

	private Function<T, T> wrap(Runnable runnable) {
		return new RunnableWrapper(runnable);
	}

	private T dispatchRun(Deque<Function<T, T>> collection, T data) {
		final Value<T> dataValue = Value.of(data);
		while (collection.peek() != null) {
			final Function<T, T> currentElement = collection.removeFirst();
			try {
				final T tempData = currentElement.apply(dataValue.get());
				dataValue.set(tempData);
			} catch (final Throwable throwable) {
				core.remove(currentElement);
			}
		}

		output.transfer(dataValue);
		return dataValue.get();
	}

	@Override
	public void add(Runnable runnable) {
		addFirst(runnable);
	}

	@Override
	public void add(Consumer<T> consumer) {
		addFirst(consumer);
	}

	@Override
	public void add(Function<T, T> function) {
		addFirst(function);
	}

	@Override
	public void addFirst(Runnable runnable) {
		addFirst(wrap(runnable));
	}

	@Override
	public void addFirst(Consumer<T> consumer) {
		addFirst(wrap(consumer));
	}

	@Override
	public void addFirst(Function<T, T> function) {
		core.addFirst(function);
	}

	@Override
	public void addLast(Runnable runnable) {
		core.addLast(wrap(runnable));
	}

	@Override
	public void addLast(Consumer<T> consumer) {
		core.addLast(wrap(consumer));
	}

	@Override
	public void addLast(Function<T, T> function) {
		core.addLast(function);
	}

	@Override
	public T apply(T input) {
		final Deque<Function<T, T>> copy;
		synchronized (core) {
			copy = new ArrayDeque<>(core);
		}

		return dispatchRun(copy, input);
	}

	@Override
	public void breakConnections() {
		output.breakUp();
		input.breakUp();
	}

	@Override
	public void clear() {
		core.clear();
	}

	@Override
	public boolean isEmpty() {
		return core.isEmpty();
	}

	@Override
	public PipelineConnection<T> input() {
		return input;
	}

	@Override
	public PipelineConnection<T> output() {
		return output;
	}

	private final class RunnableWrapper implements Function<T, T> {

		private final Runnable runnable;

		private RunnableWrapper(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public T apply(T t) {
			runnable.run();
			return t;
		}

		@Override
		public int hashCode() {

			return Objects.hash(runnable);
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (object == null || getClass() != object.getClass()) return false;
			RunnableWrapper that = (RunnableWrapper) object;
			return Objects.equals(runnable, that.runnable);
		}


	}

	private final class ConsumerWrapper implements Function<T, T> {

		private final Consumer<T> consumer;

		private ConsumerWrapper(Consumer<T> consumer) {
			this.consumer = consumer;
		}

		@Override
		public T apply(T t) {
			consumer.accept(t);
			return t;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (object == null || getClass() != object.getClass()) return false;
			ConsumerWrapper that = (ConsumerWrapper) object;
			return Objects.equals(consumer, that.consumer);
		}

		@Override
		public int hashCode() {

			return Objects.hash(consumer);
		}
	}
}
