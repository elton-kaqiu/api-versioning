package com.novario.apiversioning.configs;

import com.novario.apiversioning.handlers.VersionRequestMappingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Spring configuration class that replaces the default
 * {@link RequestMappingHandlerMapping} with a custom implementation
 * {@link VersionRequestMappingHandler}.
 *
 * <p>This ensures that controller methods annotated with
 * {@link com.novario.apiversioning.annotations.Version} are processed
 * correctly, so that API endpoints are prefixed with the configured
 * base path and version.</p>
 *
 * <p>The base path is read from the property
 * <code>api.base-path</code> (default: <code>/api</code>).</p>
 *
 * <p>Usage:
 * <ul>
 *   <li>Class-level <code>@Version("v1")</code> → endpoints under <code>/api/v1/...</code></li>
 *   <li>Method-level <code>@Version("v2")</code> → overrides class-level</li>
 *   <li>Method-level <code>@Version()</code> (blank) → explicitly unversioned (<code>/api/.../</code>)</li>
 * </ul>
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcRegistrations {

    private final Environment environment;

    /**
     * Override Spring Boot's default handler mapping with our custom
     * {@link VersionRequestMappingHandler} that understands @Version.
     *
     * @return a customized {@link RequestMappingHandlerMapping}
     */
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new VersionRequestMappingHandler(environment);
    }
}
