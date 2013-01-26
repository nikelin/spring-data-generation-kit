package com.redshape.generators.annotations;

/**
 * Created with IntelliJ IDEA.
 * User: cyril
 * Date: 11/20/12
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public @interface NativeQuery {

    public String value();

    public String name();

    public Class<?> resultType() default Object.class;

    public Parameter[] parameters() default {};

    public boolean isPageable() default false;

    public boolean isCollection() default true;

    public boolean isModifying() default false;

}
