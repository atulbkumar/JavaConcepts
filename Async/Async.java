import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Simple runtime `@Async` annotation for demo purposes.
 * Methods annotated with this will be invoked by `AsyncRunner`.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Async {
    /** If true, the runner will wait for the method's execution to complete. */
    boolean waitForCompletion() default false;
}
