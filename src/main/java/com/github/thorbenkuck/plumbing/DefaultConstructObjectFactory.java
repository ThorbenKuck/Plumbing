package com.github.thorbenkuck.plumbing;

import com.github.thorbenkuck.plumbing.exceptions.FactoryException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class DefaultConstructObjectFactory implements ObjectFactory {
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
