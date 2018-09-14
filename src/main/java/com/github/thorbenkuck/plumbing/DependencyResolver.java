package com.github.thorbenkuck.plumbing;

public interface DependencyResolver {

	<T> T create(Class<T> type);

}
