package in.kmsquare.samples.embeddingjs;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

public class GraalJsWithEngineAndSourceReuse {

    static volatile Engine engine = null;
    static volatile Source jsSource = null;
    static {
        String jsSourceString = null;
        try (InputStream is = GraalJsWithNoOptimizations.class.getResourceAsStream("/loop_for_500.mjs")) {
            jsSourceString = new String(is.readAllBytes());
            System.out.println("JS Source Loaded into String");
        } catch (IOException e) {
        }
        engine = Engine.newBuilder("js")
                .option("engine.Compilation", "true")
                .allowExperimentalOptions(true)
                .build();
        try {
            jsSource = Source.newBuilder("js", jsSourceString, "loop_for_500.mjs").build();
        } catch (IOException e) {
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Threads(1)
    public void average_time() {
        try (Context context = Context
                .newBuilder()
                .engine(engine)
                .allowExperimentalOptions(true)
                .allowAllAccess(true).build()) {
            context.eval(jsSource);
        }
    }

}
