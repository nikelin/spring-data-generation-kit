package com.redshape.generators.annotations;

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
public @interface ConventionalQueries {

    public ConventionalQuery[] value() default {};

}
