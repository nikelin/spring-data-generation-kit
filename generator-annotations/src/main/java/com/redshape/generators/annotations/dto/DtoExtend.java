package com.redshape.generators.annotations.dto;


import com.redshape.generators.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation can be used to extend resulting DTO with synthetic fields
 * which not present in original domain object.
 *
 * It's can be very useful in representing domain object meta fields or aggregated
 * operations results ( related objects count, etc. )
 *
 * @author nikelin
 * @date 21:45
 */
@Target(ElementType.TYPE)
public @interface DtoExtend {

    public Parameter[] value();

}
