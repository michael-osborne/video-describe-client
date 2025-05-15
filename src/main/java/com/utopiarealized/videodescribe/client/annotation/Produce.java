package com.utopiarealized.videodescribe.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/** Producers are responsible for creating the initial data that will be consumed by the pipeline 
 * Unline consumers, producers need to self throttle.
*/
public @interface Produce {
    // number of threads to use for the producer
    // It may make sense to have this configuration driven, but this works for MVP
    int threads() default 1;
}
