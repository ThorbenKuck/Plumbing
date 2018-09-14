package test;

import com.github.thorbenkuck.plumbing.PipelineRepository;
import com.github.thorbenkuck.plumbing.pipeline.Pipeline;

public class Test {

	public static void main(String[] args) throws Exception {
		PipelineRepository.initialize();
		PipelineRepository pipelineRepository = PipelineRepository.open();
		Pipeline<TestObject> pipeline = pipelineRepository.accessPipeline(TestObject.class);

		pipeline.apply(new TestObject());
	}

}
