package com.github.thorbenkuck.plumbing;

import com.github.thorbenkuck.plumbing.pipeline.Pipeline;
import org.junit.Test;

import java.util.function.Consumer;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class DifferentOutputTest {

	@Test
	public void test() {
		Pipeline<Integer> integerPipeline = Pipeline.open();
		Pipeline<String> stringPipeline = Pipeline.open();

		integerPipeline.add((Consumer<Integer>) System.out::println);
		stringPipeline.add((Consumer<String>) System.out::println);

		integerPipeline.output().add(stringPipeline, new TypeConverter<Integer, String>() {
			@Override
			public String convert(Integer integer) {
				return String.valueOf(integer);
			}

			@Override
			public Integer invert(String s) {
				return Integer.valueOf(s);
			}
		});

		assertFalse(integerPipeline.output().isEmpty());
		assertTrue(integerPipeline.input().isEmpty());
		assertTrue(stringPipeline.output().isEmpty());
		assertFalse(stringPipeline.input().isEmpty());

		integerPipeline.apply(10);
		integerPipeline.breakConnections();

		assertTrue(integerPipeline.output().isEmpty());
		assertTrue(integerPipeline.input().isEmpty());
		assertTrue(stringPipeline.output().isEmpty());
		assertTrue(stringPipeline.input().isEmpty());

		System.out.println(integerPipeline.output());
		System.out.println(integerPipeline.input());

	}

}
