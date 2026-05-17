import java.util.concurrent.CompletableFuture;

public class SimpleAsyncExample {
    public static void main(String[] args) {
        System.out.println("Main thread started.");

        // Start a simple async task.
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "Hello from the async task!";
        });

        // Attach a callback to handle the result when it is ready.
        future.thenAccept(result -> System.out.println("Async result: " + result));

        System.out.println("Main thread is free to do other work...");

        // Wait for the async task to finish before exiting the program.
        future.join();

        System.out.println("Main thread finished.");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
