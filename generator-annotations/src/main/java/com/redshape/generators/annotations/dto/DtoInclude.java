package com.redshape.generators.annotations.dto;

/**
 * @author nikelin
 * @date 15:11
 */
public @interface DtoInclude {

    public AggregationType value();

    public String name() default "";

}
