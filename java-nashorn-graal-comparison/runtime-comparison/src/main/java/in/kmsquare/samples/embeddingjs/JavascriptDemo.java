package in.kmsquare.samples.embeddingjs;

import org.graalvm.polyglot.Context;

// For https://kmsquare.in/blog/running-graaljs-and-optimizing-for-performance/

public class JavascriptDemo {
    public static void main(String[] args) {
        int a = 10;
        int b = 10;
        int c;
        try (org.graalvm.polyglot.Context context = Context
                .newBuilder()
                .allowExperimentalOptions(true)
                .option("engine.Compilation", "false")
                .build()) {
            context.initialize("js");
            context.getBindings("js").putMember("a", a);
            context.getBindings("js").putMember("b", b);
            c = context.eval("js", "const c = a + b; c").asInt();
            System.out.println(String.format("%d + %d = %d", a, b, c));
        }
    }
}
