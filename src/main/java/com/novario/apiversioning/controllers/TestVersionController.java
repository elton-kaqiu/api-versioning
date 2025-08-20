package com.novario.apiversioning.controllers;

import com.novario.apiversioning.annotations.Version;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example controller that demonstrates how to use the {@link Version} annotation
 * for API versioning.
 *
 * <p>At the class level this controller is annotated with {@code @Version("v1")},
 * which means that all endpoints default to being served under
 * {@code /api/v1/test/...}.</p>
 *
 * <p>Endpoints can override this behavior:
 * <ul>
 *   <li>Method-level {@code @Version("v2")} → served under {@code /api/v2/test/...}</li>
 *   <li>Method-level {@code @Version()} (blank) → explicitly unversioned,
 *       served under {@code /api/test/...}</li>
 * </ul>
 * </p>
 *
 * <p>This controller is meant for testing and verifying that
 * {@link com.novario.apiversioning.handlers.VersionRequestMappingHandler}
 * correctly processes versioning rules.</p>
 */
@RestController
@RequestMapping("/test")
@Version("v1") // Default: all methods inherit v1 unless overridden
public class TestVersionController {

    /**
     * Inherits version "v1" from the class-level annotation.
     * URL: /api/v1/test/hello
     */
    @GetMapping("/hello")
    public String helloV1() {
        return "Hello from v1 (class-level)";
    }

    /**
     * Inherits version "v1" from the class-level annotation.
     * URL: /api/v1/test/ping
     */
    @GetMapping("/ping")
    public String pingV1() {
        return "Ping v1 OK";
    }

    /**
     * Overrides the class-level version with "v2".
     * URL: /api/v2/test/hello
     */
    @Version("v2")
    @GetMapping("/hello")
    public String helloV2() {
        return "Hello from v2 (method-level override)";
    }

    /**
     * Explicitly unversioned because of @Version() with no value.
     * Ignores the class-level version "v1".
     * URL: /api/test/info
     */
    @Version("")
    @GetMapping("/info")
    public String info() {
        return "Unversioned endpoint";
    }
}
