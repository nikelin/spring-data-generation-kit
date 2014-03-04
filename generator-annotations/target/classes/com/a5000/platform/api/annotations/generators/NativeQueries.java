package com.a5000.platform.api.annotations.generators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 */
@Target(ElementType.TYPE)
public @interface NativeQueries {

    public NativeQuery[] value();

    public boolean isTransactional() default false;

}
