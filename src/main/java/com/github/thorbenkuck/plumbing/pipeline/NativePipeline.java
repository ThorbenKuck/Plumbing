package com.github.thorbenkuck.plumbing.pipeline;

import com.github.thorbenkuck.plumbing.lang.Value;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

final class NativePipeline<T> implements Pipeline<T> {

	private final Deque<Function<T, T>> core = new ArrayDeque<>();
	private final PipelineConnection<T> input = PipelineConnection.input(this);
	private final PipelineConnection<T> output = PipelineConnection.output(this);
	private final Value<Boolean> readOutputTransfer = Value.synchronize(false);

	private Function<T, T> wrap(final Consumer<T> consumer) {
		return new ConsumerWrapper(consumer);
	}

	private Function<T, T> wrap(final Runnable runnable) {
		return new RunnableWrapper(runnable);
	}

	private T dispatchRun(final Deque<Function<T, T>> collection, final T data) {
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

		if (readOutputTransfer.get()) {
			output.transfer(dataValue);
		} else {
			output.transfer(dataValue.get());
		}
		return dataValue.get();
	}

	@Override
	public final void add(final Runnable runnable) {
		addFirst(runnable);
	}

	@Override
	public final void add(final Consumer<T> consumer) {
		addFirst(consumer);
	}

	@Override
	public final void add(final Function<T, T> function) {
		addFirst(function);
	}

	@Override
	public final void addFirst(final Runnable runnable) {
		addFirst(wrap(runnable));
	}

	@Override
	public final void addFirst(final Consumer<T> consumer) {
		addFirst(wrap(consumer));
	}

	@Override
	public final void addFirst(final Function<T, T> function) {
		core.addFirst(function);
	}

	@Override
	public final void addLast(final Runnable runnable) {
		core.addLast(wrap(runnable));
	}

	@Override
	public final void addLast(final Consumer<T> consumer) {
		core.addLast(wrap(consumer));
	}

	@Override
	public final void addLast(final Function<T, T> function) {
		core.addLast(function);
	}

	@Override
	public final T apply(T input) {
		final Deque<Function<T, T>> copy;
		synchronized (core) {
			copy = new ArrayDeque<>(core);
		}

		return dispatchRun(copy, input);
	}

	@Override
	public final void breakConnections() {
		output.breakUp();
		input.breakUp();
	}

	@Override
	public final void clear() {
		core.clear();
	}

	@Override
	public final boolean isEmpty() {
		return core.isEmpty();
	}

	@Override
	public final PipelineConnection<T> input() {
		return input;
	}

	@Override
	public final PipelineConnection<T> output() {
		return output;
	}

	private final class RunnableWrapper implements Function<T, T> {

		private final Runnable runnable;

		private RunnableWrapper(final Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public final T apply(T t) {
			runnable.run();
			return t;
		}

		@Override
		public final int hashCode() {
			return Objects.hash(runnable);
		}

		@Override
		public final boolean equals(final Object object) {
			if (this == object) return true;
			if (object == null || getClass() != object.getClass()) return false;
			final RunnableWrapper that = (RunnableWrapper) object;
			return Objects.equals(runnable, that.runnable);
		}


	}

	private final class ConsumerWrapper implements Function<T, T> {

		private final Consumer<T> consumer;

		private ConsumerWrapper(final Consumer<T> consumer) {
			this.consumer = consumer;
		}

		@Override
		public final T apply(final T t) {
			consumer.accept(t);
			return t;
		}

		@Override
		public final boolean equals(final Object object) {
			if (this == object) return true;
			if (object == null || getClass() != object.getClass()) return false;
			final ConsumerWrapper that = (ConsumerWrapper) object;
			return Objects.equals(consumer, that.consumer);
		}

		@Override
		public final int hashCode() {
			return Objects.hash(consumer);
		}
	}
}
