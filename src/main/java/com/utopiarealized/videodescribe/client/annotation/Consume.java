package com.utopiarealized.videodescribe.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Consume {
    // number of threads to use for the consumer
    // It may make sense to have this configuration driven, but this works for MVP
    int threads() default 1;

}