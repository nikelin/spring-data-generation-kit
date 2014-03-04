package com.a5000.platform.api.annotations.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by cyril on 8/28/13.
 */
@Target(ElementType.METHOD)
public @interface DtoMethod {

    public boolean isAbstract() default true;

}
