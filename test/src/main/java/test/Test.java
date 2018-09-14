package test;

import com.github.thorbenkuck.plumbing.PipelineRepository;
import com.github.thorbenkuck.plumbing.pipeline.Pipeline;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public final class Test {

	public static void main(String[] args) throws Exception {
		Injector injector = Guice.createInjector(new TestModule());
		PipelineRepository pipelineRepository = PipelineRepository.open();

		pipelineRepository.setDependencyResolver(new GuiceObjectFactory(injector));
		pipelineRepository.load();

		Pipeline<TestObject> pipeline = pipelineRepository.accessPipeline(TestObject.class);

		pipeline.apply(new TestObject());
	}

	private static final class TestModule extends AbstractModule {
		public void configure() {
			bind(TestObjectHandler2.class).asEagerSingleton();
		}
	}

}
