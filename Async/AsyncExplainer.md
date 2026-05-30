Async Annotation & CompletableFuture — Explainer
=============================================

Purpose
-------
This document explains the simple `@Async` demo in this workspace and contrasts it with using `CompletableFuture` directly. It's intended to help you understand design trade-offs and how the provided code works.

Files to inspect
- [Async.java](Async.java) — runtime annotation definition.
- [AsyncRunner.java](AsyncRunner.java) — reflection-based runner that finds and executes `@Async` methods.
- [AsyncAnnotationExample.java](AsyncAnnotationExample.java) — demo class with example tasks annotated with `@Async`.
- [CompletableFutureDemo.java](CompletableFutureDemo.java) — separate demo using `CompletableFuture` chains.

What the `@Async` demo shows
-----------------------------
- `@Async` is a marker annotation: by itself it does nothing. Behavior is provided by `AsyncRunner`.
- `AsyncRunner.runAsyncMethods(target)` uses reflection to find no-arg methods annotated with `@Async` and submits them to an `ExecutorService`.
- Methods with `waitForCompletion = true` cause the runner to block until that method's `Future` completes; otherwise they run fire-and-forget.
- The demo uses a cached thread pool: new tasks may create new threads named like `pool-1-thread-#`.

Why use an annotation vs `CompletableFuture` directly
----------------------------------------------------
- Annotation approach (this demo):
  - Pros: simple to mark many methods declaratively; good for quick demos or small tasks.
  - Cons: needs reflection or AOP to wire behavior; limited type-safety and error handling; harder to compose results.
- `CompletableFuture` approach (recommended for real async flows):
  - Pros: explicit, composable, integrates with reactive pipelines and exception handling; supports return values and chaining.
  - Cons: more verbose when you only need simple fire-and-forget behavior.

Example behaviors
-----------------
- `@Async` method with `waitForCompletion=false` → submitted to pool, caller does not block.
- `@Async(waitForCompletion=true)` → runner waits for that specific method to finish before returning.
- `CompletableFuture.supplyAsync(...)` → returns a `CompletableFuture<T>` you can `thenApply`, `thenCompose`, `whenComplete`, or `get` on.

How to run the demos
--------------------
Open a terminal and run:

```powershell
cd "c:\Programming\JavaConcepts\Async"
javac *.java
java AsyncAnnotationExample
```

For the CompletableFuture demo (it's in a package), run:

```powershell
javac *.java
java com.example.demo.CompletableFutureDemo
```

Interpreting output
-------------------
- Look at thread names to see which tasks ran on worker threads vs `main`.
- The order of `quickTask`, `slowTask`, and `importantTask` is not guaranteed; only `waitForCompletion` enforces a local wait.

Next steps (suggestions)
-----------------------
- Extend `AsyncRunner` to support methods that return `CompletableFuture` and automatically compose them.
- Replace reflection with a small AOP example (Spring `@Async`) to show production-grade wiring.
- Add logging timestamps and better shutdown handling to make timing behavior clearer.

Questions?
----------
Tell me which next step you'd like and I can implement it: extending `AsyncRunner`, adding a Spring/AOP example, or enhancing the `CompletableFuture` demo.
