package com.redshape.generators.annotations;

/**
 * Created with IntelliJ IDEA.
 * User: cyril
 * Date: 11/20/12
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public @interface ConventionalQuery {

    public String name();

    public Class<?> resultType() default Object.class;

    public boolean isPageable() default false;

    public boolean isCollection() default true;

    public Parameter[] parameters() default {};

}
