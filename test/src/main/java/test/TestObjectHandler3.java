package test;

import java.util.function.Function;

public final class TestObjectHandler3 implements Function<TestObject, TestObject> {
	@Override
	public TestObject apply(TestObject testObject) {
		System.out.println("Hello from TestObjectHandler3! TestObject=" + testObject);

		return testObject;
	}
}
