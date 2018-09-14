package test;

import com.github.thorbenkuck.plumbing.DefaultConstructObjectFactory;
import com.github.thorbenkuck.plumbing.ObjectFactory;
import com.github.thorbenkuck.plumbing.exceptions.FactoryException;
import com.google.inject.Injector;

public final class GuiceObjectFactory implements ObjectFactory {

	private final Injector injector;
	private final DefaultConstructObjectFactory defaultConstructObjectFactory = new DefaultConstructObjectFactory();

	public GuiceObjectFactory(Injector injector) {
		this.injector = injector;
	}

	@Override
	public <T> T create(Class<T> type) throws FactoryException {
		final T t = injector.getInstance(type);
		if (t == null) {
			return defaultConstructObjectFactory.create(type);
		}
		return t;
	}
}
