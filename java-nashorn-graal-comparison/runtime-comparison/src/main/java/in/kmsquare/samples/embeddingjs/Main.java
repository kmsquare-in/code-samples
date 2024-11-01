package in.kmsquare.samples.embeddingjs;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Main {
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(GraalJsWithJavaInteractionAndNoOptimizations.class.getSimpleName())
                .include(GraalJsWithJavaInteractionEngineAndSourceReuse.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}
