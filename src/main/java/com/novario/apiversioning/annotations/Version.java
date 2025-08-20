package com.novario.apiversioning.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotation to specify an API version for a controller or a controller method.
 * <p>
 * Can be applied at the class level (applies to all endpoints in that controller)
 * or at the method level (overrides class-level version).
 * <p>
 * Example:
 * <pre>
 * {@code
 * @RestController
 * @RequestMapping("/test")
 * @Version("v1")   // All methods in this controller are under /api/v1
 * public class TestController {
 *
 *     @GetMapping("/hello")
 *     public String helloV1() { return "v1"; }
 *
 *     @Version("v2")   // This method is under /api/v2, overriding class-level
 *     @GetMapping("/hello")
 *     public String helloV2() { return "v2"; }
 * }
 * }
 * </pre>
 */
@Documented                                    // included in Javadoc
@Target({ElementType.TYPE, ElementType.METHOD}) // usable on class or method
@Retention(RetentionPolicy.RUNTIME)            // available at runtime for reflection
public @interface Version {

    /**
     * The version name (e.g., "v1", "v2").
     * <p>
     * This is an alias for {@link #value()}, meaning you can use either.
     * <p>
     * Example: {@code @Version(name = "v1")}
     */
    @AliasFor("value")
    String name() default "";

    /**
     * The version value (e.g., "v1", "v2").
     * <p>
     * This is an alias for {@link #name()}, meaning you can use either.
     * <p>
     * Example: {@code @Version("v1")}
     */
    @AliasFor("name")
    String value() default "";
}
