import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletableFutureDemo {
    public static void main(String[] args) {
        System.out.println("Starting CompletableFuture demo...");

        // 1. Create an asynchronous task that returns a string after a short delay.
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "Hello from supplyAsync";
        });

        // 2. Chain a callback that transforms the result.
        CompletableFuture<String> transformed = future.thenApply(result -> {
            System.out.println("Original result: " + result);
            return result + " -> transformed by thenApply";
        });

        // 3. Add a second asynchronous stage that runs after the first one completes.
        CompletableFuture<String> composed = transformed.thenCompose(result ->
                CompletableFuture.supplyAsync(() -> result + " -> thenCompose adds more"));

        // 4. Handle success and failure with whenComplete.
        CompletableFuture<String> handled = composed.whenComplete((result, error) -> {
            if (error != null) {
                System.err.println("Computation failed: " + error.getMessage());
            } else {
                System.out.println("Completed successfully with: " + result);
            }
        });

        // 5. Use exceptionally to provide a fallback value in case of exception.
        CompletableFuture<String> fallback = handled.exceptionally(error -> {
            return "Fallback value due to: " + error.getMessage();
        });

        // 6. Block and get the final value for demonstration.
        try {
            String finalResult = fallback.get(3, TimeUnit.SECONDS);
            System.out.println("Final result: " + finalResult);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.err.println("Error waiting for result: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        System.out.println("Demo complete.");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
