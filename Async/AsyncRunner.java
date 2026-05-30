import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Small utility that finds methods annotated with `@Async` on an object
 * and invokes them asynchronously using a cached thread pool.
 */
public class AsyncRunner {
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void runAsyncMethods(Object target) {
        if (target == null) return;

        Method[] methods = target.getClass().getDeclaredMethods();
        List<Future<?>> futuresToWait = new ArrayList<>();

        for (Method m : methods) {
            Async ann = m.getAnnotation(Async.class);
            if (ann == null) continue;

            // Only allow no-arg methods in this simple demo.
            if (m.getParameterCount() != 0) continue;

            m.setAccessible(true);
            Runnable task = () -> {
                try {
                    m.invoke(target);
                } catch (Exception e) {
                    System.err.println("Error invoking async method: " + e.getMessage());
                }
            };

            Future<?> f = executor.submit(task);
            if (ann.waitForCompletion()) {
                futuresToWait.add(f);
            }
        }

        // Wait for any methods that requested completion.
        for (Future<?> f : futuresToWait) {
            try {
                f.get();
            } catch (Exception e) {
                System.err.println("Error waiting for async method: " + e.getMessage());
            }
        }
    }

    // Add shutdown hook for demo cleanliness; not strictly necessary.
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> executor.shutdownNow()));
    }
}
