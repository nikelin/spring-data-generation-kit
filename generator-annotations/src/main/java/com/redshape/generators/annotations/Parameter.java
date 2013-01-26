package com.redshape.generators.annotations;

/**
 * Created with IntelliJ IDEA.
 * User: cyril
 * Date: 11/20/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public @interface Parameter {

    /**
     * Name of field associated with and represented by this annotation
     * which belongs to type referenced by {@link com.redshape.odd.data.annotations.Parameter#type()}
     *
     * In cases when value provided as an empty string the generated one selected, for example:
     * - param1
     * - param2
     * ....
     *
     *
     * @return
     */
    public String value() default "";

    /**
     * Type of requested parameter referenced by {@link com.redshape.odd.data.annotations.Parameter#value()}
     *
     * @return {@link Class}
     */
    public Class<?> type();

    public boolean isArray() default false;

}
