package com.novario.apiversioning.handlers;

import com.novario.apiversioning.annotations.Version;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class VersionRequestMappingHandler extends RequestMappingHandlerMapping {
    private final Environment environment;

    private static final String UNVERSIONED = "__UNVERSIONED__";

    private static String resolveFromMethod(Method m) {
        var ann = MergedAnnotations.from(m, MergedAnnotations.SearchStrategy.DIRECT).get(Version.class);
        if (!ann.isPresent()) return null;
        Version v = ann.synthesize();
        // Explicit blank → unversioned
        if (v.value().isBlank() && v.name().isBlank()) return UNVERSIONED;
        return pick(v);
    }

    private static String resolveFromType(Class<?> t) {
        var ann = MergedAnnotations.from(t, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(Version.class);
        return ann.isPresent() ? pick(ann.synthesize()) : null;
    }

    private static String pick(Version v) {
        String s = !v.name().isBlank() ? v.name() : v.value();
        return s.isBlank() ? null : s;
    }

    private static String normalize(String p) {
        if (p == null || p.isEmpty()) return "";
        String s = p.startsWith("/") ? p : "/" + p;
        return s.endsWith("/") && s.length() > 1 ? s.substring(0, s.length() - 1) : s;
    }

    private static String stripSlashes(String s) {
        String r = s.trim();
        if (r.startsWith("/")) r = r.substring(1);
        if (r.endsWith("/")) r = r.substring(0, r.length() - 1);
        return r;
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(@NonNull Method method, @NonNull Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info == null) return null;

        String ver = resolveFromMethod(method);

        // If method explicitly says unversioned, skip prefix
        if (UNVERSIONED.equals(ver)) {
            String base = normalize(environment.getProperty("api.base-path", "/api"));
            RequestMappingInfo prefix = RequestMappingInfo
                    .paths(base)
                    .options(getBuilderConfiguration())
                    .build();
            return prefix.combine(info);
        }


        // Otherwise, fallback to class-level if method didn't specify
        if (ver == null) ver = resolveFromType(handlerType);

        if (ver == null) return info;

        String base = normalize(environment.getProperty("api.base-path", "/api"));
        String prefixPath = base + "/" + stripSlashes(ver);

        RequestMappingInfo prefix = RequestMappingInfo
                .paths(prefixPath)
                .options(getBuilderConfiguration())
                .build();

        return prefix.combine(info);
    }
}
