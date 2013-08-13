package com.redshape.generators.annotations.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @user Nikelin
 * @date 27.01.13
 * @time 21:02
 * @package com.redshape.generators.annotations.dto
 */
@Target(ElementType.FIELD)
public @interface DtoFieldOverride {

    public String name() default "";

    public Class<?> type() default Void.class;

}
