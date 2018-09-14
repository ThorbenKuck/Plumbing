package test;

import java.util.function.Consumer;

public class TestObjectHandler2 implements Consumer<TestObject> {

	public TestObjectHandler2(TestDependency testDependency) {

	}

	@Override
	public void accept(TestObject testObject) {
		System.out.println("Hello from TestObjectHandler2! TestObject=" + testObject);
	}
}
