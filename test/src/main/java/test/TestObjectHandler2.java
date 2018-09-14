package test;

import com.google.inject.Inject;

import java.util.function.Consumer;

public final class TestObjectHandler2 implements Consumer<TestObject> {

	@Inject
	public TestObjectHandler2(TestDependency testDependency) {

	}

	@Override
	public void accept(TestObject testObject) {
		System.out.println("Hello from TestObjectHandler2! TestObject=" + testObject);
	}
}
