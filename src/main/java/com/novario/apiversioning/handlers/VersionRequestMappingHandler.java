package com.novario.apiversioning.handlers;

import com.novario.apiversioning.annotations.Version;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * Custom {@link RequestMappingHandlerMapping} that applies API versioning rules
 * based on the {@link Version} annotation.
 *
 * <p>How it works:</p>
 * <ul>
 *   <li>Class-level {@code @Version("v1")} → all methods default to version "v1".</li>
 *   <li>Method-level {@code @Version("v2")} → overrides class-level version.</li>
 *   <li>Method-level {@code @Version()} (blank) → explicitly unversioned, mapped only under the base path.</li>
 *   <li>If no {@code @Version} is present at all → endpoint is unversioned, mapped only under the base path.</li>
 * </ul>
 *
 * <p>The base API path is configurable with the property
 * <code>api.base-path</code> (default: <code>/api</code>).</p>
 *
 * <p>Examples with <code>api.base-path=/api</code>:</p>
 * <ul>
 *   <li><code>/api/v1/test/hello</code> → class-level version v1</li>
 *   <li><code>/api/v2/test/hello</code> → method-level override v2</li>
 *   <li><code>/api/test/info</code> → explicitly unversioned method</li>
 * </ul>
 */
@RequiredArgsConstructor
public class VersionRequestMappingHandler extends RequestMappingHandlerMapping {

    private final Environment environment;

    /**
     * Special marker for explicitly unversioned endpoints.
     */
    private static final String UNVERSIONED = "__UNVERSIONED__";

    /**
     * Resolves the version annotation from a method.
     *
     * @param m the handler method
     * @return version string, {@link #UNVERSIONED} if explicitly unversioned,
     * or {@code null} if no annotation present
     */
    private static String resolveFromMethod(Method m) {
        var ann = MergedAnnotations.from(m, MergedAnnotations.SearchStrategy.DIRECT).get(Version.class);
        if (!ann.isPresent()) return null;
        Version v = ann.synthesize();
        // Explicit blank means unversioned
        if (v.value().isBlank() && v.name().isBlank()) return UNVERSIONED;
        return pick(v);
    }

    /**
     * Resolves the version annotation from a class.
     *
     * @param t the handler type
     * @return version string, or {@code null} if no annotation present
     */
    private static String resolveFromType(Class<?> t) {
        var ann = MergedAnnotations.from(t, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(Version.class);
        return ann.isPresent() ? pick(ann.synthesize()) : null;
    }

    /**
     * Picks the version string from the annotation, checking both name and value.
     */
    private static String pick(Version v) {
        String s = !v.name().isBlank() ? v.name() : v.value();
        return s.isBlank() ? null : s;
    }

    /**
     * Normalizes a path string by ensuring a leading "/" and removing trailing "/".
     */
    private static String normalize(String p) {
        if (p == null || p.isEmpty()) return "";
        String s = p.startsWith("/") ? p : "/" + p;
        return s.endsWith("/") && s.length() > 1 ? s.substring(0, s.length() - 1) : s;
    }

    /**
     * Strips leading and trailing slashes from a string.
     */
    private static String stripSlashes(String s) {
        String r = s.trim();
        if (r.startsWith("/")) r = r.substring(1);
        if (r.endsWith("/")) r = r.substring(0, r.length() - 1);
        return r;
    }

    /**
     * Builds the mapping for each handler method, applying version prefixes if needed.
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(@NonNull Method method, @NonNull Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info == null) return null;

        String ver = resolveFromMethod(method);

        // Case 1: explicitly unversioned (method has @Version() blank)
        if (UNVERSIONED.equals(ver)) {
            String base = normalize(environment.getProperty("api.base-path", "/api"));
            RequestMappingInfo prefix = RequestMappingInfo
                    .paths(base)
                    .options(getBuilderConfiguration())
                    .build();
            return prefix.combine(info);
        }

        // Case 2: fallback to class-level if method didn’t specify
        if (ver == null) ver = resolveFromType(handlerType);

        // Case 3: no version at all → unversioned (only base path)
        if (ver == null) {
            String base = normalize(environment.getProperty("api.base-path", "/api"));
            RequestMappingInfo prefix = RequestMappingInfo
                    .paths(base)
                    .options(getBuilderConfiguration())
                    .build();
            return prefix.combine(info);
        }

        // Case 4: versioned → add base path + version
        String base = normalize(environment.getProperty("api.base-path", "/api"));
        String prefixPath = base + "/" + stripSlashes(ver);

        RequestMappingInfo prefix = RequestMappingInfo
                .paths(prefixPath)
                .options(getBuilderConfiguration())
                .build();

        return prefix.combine(info);
    }
}
