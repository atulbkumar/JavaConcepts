Async annotation demo
=====================

This small demo shows a lightweight `@Async` annotation and an `AsyncRunner`
that invokes annotated no-arg methods on an object using a thread pool.

Compile:

```powershell
javac *.java
```

Run the `AsyncAnnotationExample`:

```powershell
java AsyncAnnotationExample
```

Also see `CompletableFutureDemo.java` for a separate demo using
`CompletableFuture` APIs.
