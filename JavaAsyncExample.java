import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JavaAsyncExample {
    public static void main(String[] args) {
        System.out.println("Starting async programming example...");

        // 1) Start two independent asynchronous tasks.
        CompletableFuture<String> fetchUser = CompletableFuture.supplyAsync(() -> {
            sleep(800);
            return "Alice";
        });

        CompletableFuture<String> fetchOrders = CompletableFuture.supplyAsync(() -> {
            sleep(1200);
            return "Book, Pen, Notebook";
        });

        // 2) Transform the user result once it arrives.
        CompletableFuture<String> transformedUser = fetchUser.thenApply(user -> "User: " + user);

        // 3) Combine two async results to produce a single summary string.
        CompletableFuture<String> summary = transformedUser.thenCombine(fetchOrders,
                (user, orders) -> user + " has orders: " + orders);

        // 4) Consume the final result without returning a new value.
        CompletableFuture<Void> printResult = summary.thenAccept(result -> {
            System.out.println("Result received in callback:");
            System.out.println(result);
        });

        // 5) Add error handling and a fallback value.
        CompletableFuture<String> safeSummary = summary.exceptionally(error -> {
            System.err.println("Error building summary: " + error.getMessage());
            return "Unable to build async summary.";
        });

        // 6) Use whenComplete to log success or failure.
        safeSummary.whenComplete((result, error) -> {
            if (error != null) {
                System.err.println("Final stage failed: " + error.getMessage());
            } else {
                System.out.println("Final summary ready: " + result);
            }
        });

        System.out.println("Main thread continues while async tasks run...");

        // 7) Wait for all async pieces to finish for demo purposes.
        try {
            CompletableFuture.allOf(fetchUser, fetchOrders, printResult).get(3, TimeUnit.SECONDS);
            System.out.println("All async work finished.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while waiting for async tasks.");
        } catch (ExecutionException | TimeoutException e) {
            System.err.println("Waiting failed: " + e.getMessage());
        }

        System.out.println("Async programming example complete.");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
