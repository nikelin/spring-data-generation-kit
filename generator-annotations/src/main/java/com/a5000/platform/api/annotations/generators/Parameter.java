package com.a5000.platform.api.annotations.generators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Copyright 2016 Cyril A. Karpenko <self@nikelin.ru>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    String value() default "";

    /**
     * Type of requested parameter referenced by {@link Parameter#value()}
     *
     * @return {@link Class}
     */
    Class<?> type();

    /**
     * Needs because of problem in Thoughtworks QDox annotation parser which ignore "[]" in annotation
     * field type name.
     *
     * If true, then query parameter would be placed as array:
     * {@code Example findByIdIn( @Parameter("ids") long[] ids ); }
     *
     * @return
     */
    boolean isArray() default false;

    boolean isEnum() default false;

    String expression() default "";

    Class<?>[] typeParameters() default {};

}
