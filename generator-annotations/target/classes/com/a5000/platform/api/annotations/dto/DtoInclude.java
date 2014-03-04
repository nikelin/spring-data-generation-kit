package com.a5000.platform.api.annotations.dto;

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
