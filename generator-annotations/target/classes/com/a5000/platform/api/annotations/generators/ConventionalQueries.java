package com.a5000.platform.api.annotations.generators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Conventional queries collection representation.
 *
 * Each child @ConventionalQuery would be represented as a
 * separate method in the resulting spring-data repository.
 *
 * @see {@link ConventionalQuery}
 *
 * @author Cyril A. Karpenko <self@nikelin.ru>
 */
@Target(ElementType.TYPE)
public @interface ConventionalQueries {

    public ConventionalQuery[] value() default {};

    public boolean isTransactional() default false;

}
