package in.kmsquare.samples.embeddingjs;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
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

public class GraalJsWithJavaInteractionEngineAndSourceReuse {

    static final String script_name = "/find_prime_numbers_till_N.mjs";

    static public class SomeJavaService {
        final int incrementBy = 1;

        public int incrementBy1(int i) {
            return i + incrementBy;
        }
    }

    static SomeJavaService javaCxtObj = new SomeJavaService();
    static volatile Engine engine = null;
    static volatile Source jsSource = null;
    static {
        String jsSourceString = null;
        try (InputStream is = GraalJsWithJavaInteractionEngineAndSourceReuse.class.getResourceAsStream(script_name)) {
            jsSourceString = new String(is.readAllBytes());
        } catch (IOException e) {
        }
        engine = Engine.newBuilder("js")
                .option("engine.Compilation", "true")
                .allowExperimentalOptions(true)
                .build();
        try {
            jsSource = Source.newBuilder("js", jsSourceString, script_name).build();
        } catch (IOException e) {
        }
    }

    private void implementation() {
        try (Context context = Context
                .newBuilder()
                .engine(engine)
                .allowExperimentalOptions(true)
                .allowAllAccess(true).build()) {
            Value bindings = context.getBindings("js");
            bindings.putMember("javaCxtObj", javaCxtObj);
            bindings.putMember("input", 1000);
            bindings.putMember("expected", 997);
            context.eval(jsSource);
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
        implementation();
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(2)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Threads(1)
    public void single_shot_time() {
        implementation();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(2)
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Threads(1)
    public void throughput() {
        implementation();
    }

}
