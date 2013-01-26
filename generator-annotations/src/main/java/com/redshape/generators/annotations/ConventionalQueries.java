package com.redshape.generators.annotations;

/**
 * Created with IntelliJ IDEA.
 * User: cyril
 * Date: 11/23/12
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public @interface ConventionalQueries {

    public ConventionalQuery[] value() default {};

}
