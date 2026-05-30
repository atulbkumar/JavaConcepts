public class AsyncAnnotationExample {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main thread: " + Thread.currentThread().getName());

        ExampleTasks tasks = new ExampleTasks();

        // Run methods annotated with @Async. The runner will execute them concurrently.
        AsyncRunner.runAsyncMethods(tasks);

        // Give async tasks a moment to run before main exits (for demo only).
        Thread.sleep(2000);
        System.out.println("Main exiting");
    }

    public static class ExampleTasks {
        @Async
        public void quickTask() {
            System.out.println("quickTask start on " + Thread.currentThread().getName());
        }

        @Async
        public void slowTask() {
            System.out.println("slowTask start on " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("slowTask end on " + Thread.currentThread().getName());
        }

        @Async(waitForCompletion = true)
        public void importantTask() {
            System.out.println("importantTask start on " + Thread.currentThread().getName());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("importantTask end on " + Thread.currentThread().getName());
        }
    }
}