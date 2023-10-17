package io.github.mainmethod0126.search.condition.metadata.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation serves to indicate the domain class capable of generating
 * metaData in JSON Format.
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MetaData {
    String key() default "";

    int maxDepth() default 3;
}
