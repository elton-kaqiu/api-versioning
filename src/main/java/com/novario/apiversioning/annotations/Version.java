package com.novario.apiversioning.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Version {
    @AliasFor("value")
    String name() default "";

    @AliasFor("name")
    String value() default "";
}
