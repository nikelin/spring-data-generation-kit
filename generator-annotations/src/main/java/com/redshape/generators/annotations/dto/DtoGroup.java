package com.redshape.generators.annotations.dto;

/**
 * Created with IntelliJ IDEA.
 * User: cyril
 * Date: 11/20/12
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public @interface DtoGroup {

    public String value();

    public String inherits() default "";

}
