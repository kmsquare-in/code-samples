package in.kmsquare.samples.embeddingjs;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

public class GraalJsWithNoOptimizations {

    /*
     * @State(Scope.Benchmark)
     * public static class SharedJsSource {
     * volatile String jsSourceString = "";
     * volatile Engine engine;
     * volatile Source jsSource;
     * 
     * public SharedJsSource() {
     * try (InputStream is = GraalJsInterpreterModeTests.class.getResourceAsStream(
     * "/javascript_test_code.mjs")) {
     * jsSourceString = new String(is.readAllBytes());
     * System.out.println(jsSourceString);
     * } catch (IOException e) {
     * jsSourceString = "";
     * }
     * engine = Engine
     * .newBuilder("js")
     * .allowExperimentalOptions(true)
     * .option("engine.Compilation", "true")
     * .build();
     * try {
     * jsSource = Source.newBuilder("js", jsSourceString,
     * "/javascript_test_code.mjs").build();
     * } catch (Exception e) {
     * jsSource = null;
     * }
     * }
     * }
     * 
     * public void graal_js_with_optimizations(SharedJsSource sharedSourceAndEngine)
     * throws InterruptedException {
     * try (Context context = Context
     * .newBuilder()
     * .option("engine.Compilation", "true")
     * .engine(sharedSourceAndEngine.engine)
     * .allowExperimentalOptions(true)
     * .allowAllAccess(true).build()) {
     * context.eval(sharedSourceAndEngine.jsSource);
     * }
     * }
     */

    static volatile String jsSourceString = "";
    static {
        try (InputStream is = GraalJsWithNoOptimizations.class.getResourceAsStream("/loop_for_500.mjs")) {
            jsSourceString = new String(is.readAllBytes());
            System.out.println("JS Source Loaded into String");
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
    public void average_time()
            throws InterruptedException, IOException {
        try (Context context = Context
                .newBuilder()
                .option("engine.Compilation", "false")
                .allowExperimentalOptions(true)
                .allowAllAccess(true).build()) {
            context.eval(
                    Source.newBuilder("js", jsSourceString, "loop_for_500.mjs").build());
        }
    }

    /*
     * @Benchmark
     * 
     * @BenchmarkMode(Mode.AverageTime)
     * 
     * @OutputTimeUnit(TimeUnit.MILLISECONDS)
     * 
     * @Fork(2)
     * 
     * @Warmup(iterations = 5)
     * 
     * @Measurement(iterations = 5)
     * 
     * @Threads(1)
     * public void average_time_of_graal_js_with_optimizations(SharedJsSource
     * sharedSourceAndEngine)
     * throws InterruptedException {
     * graal_js_with_optimizations(sharedSourceAndEngine);
     * }
     */

}
