package com.redshape.generators.annotations.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author nikelin
 * @date 15:11
 */
@Target(ElementType.FIELD)
public @interface DtoInclude {

    public AggregationType value();

    public String name() default "";

}
