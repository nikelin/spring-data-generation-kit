package com.a5000.platform.api.annotations.generators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author Cyril A. Karpenko <self@nikelin.ru>
 */
@Target(ElementType.ANNOTATION_TYPE)
public @interface Parameter {

    /**
     * Name of field associated with and represented by this annotation
     * which belongs to type referenced by {@link com.a5000.odd.data.annotations.Parameter#type()}
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
     * Type of requested parameter referenced by {@link Parameter#value()}
     *
     * @return {@link Class}
     */
    public Class<?> type();

    /**
     * Needs because of problem in Thoughtworks QDox annotation parser which ignore "[]" in annotation
     * field type name.
     *
     * If true, then query parameter would be placed as array:
     * {@code Example findByIdIn( @Parameter("ids") long[] ids ); }
     *
     * @return
     */
    public boolean isArray() default false;

    public String expression() default "";

    Class<?>[] typeParameters() default {};
}
