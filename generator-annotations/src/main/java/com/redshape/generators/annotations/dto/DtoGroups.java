package com.redshape.generators.annotations.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: cyril
 * Date: 11/20/12
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Target(ElementType.TYPE)
public @interface DtoGroups {

    public DtoGroup[] value();

}
