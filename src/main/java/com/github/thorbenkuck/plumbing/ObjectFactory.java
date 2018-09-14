package com.github.thorbenkuck.plumbing;

import com.github.thorbenkuck.plumbing.exceptions.FactoryException;

public interface ObjectFactory {

	<T> T create(Class<T> type) throws FactoryException;

}
