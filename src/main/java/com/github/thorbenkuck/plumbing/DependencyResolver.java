package com.github.thorbenkuck.plumbing;

public interface DependencyResolver {

	<T> T create(final Class<T> type);

}
