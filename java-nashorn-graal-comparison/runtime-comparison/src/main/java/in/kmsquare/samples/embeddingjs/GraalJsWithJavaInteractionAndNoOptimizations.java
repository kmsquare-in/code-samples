package in.kmsquare.samples.embeddingjs;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

public class GraalJsWithJavaInteractionAndNoOptimizations {

    static final String script_name = "/loop_for_500_with_java_interactions.mjs";
    static volatile String jsSourceString = "";

    static public class SomeJavaService {
        final int incrementBy = 1;
        public int someFunction(int i) {
            return i+incrementBy;
        }
    }

    static SomeJavaService javaCxtObj = new SomeJavaService();
    static {
        try (InputStream is = GraalJsWithNoOptimizations.class.getResourceAsStream(script_name)) {
            jsSourceString = new String(is.readAllBytes());
        } catch (IOException e) {
        }
    }

    private void implementation() throws IOException {
        try (Context context = Context
                .newBuilder()
                .option("engine.Compilation", "false")
                .allowExperimentalOptions(true)
                .allowAllAccess(true).build()) {
            context.initialize("js");
            Value bindings = context.getBindings("js");
            bindings.putMember("javaCxtObj", javaCxtObj);
            context.eval(
                    Source.newBuilder("js", jsSourceString, script_name).build());
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Threads(1)
    public void average_time()
            throws InterruptedException, IOException {
        implementation();
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Threads(1)
    public void single_shot_time()
            throws InterruptedException, IOException {
        implementation();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(2)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Threads(1)
    public void throughput()
            throws InterruptedException, IOException {
        implementation();
    }

}
