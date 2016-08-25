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
 *
 *
 * Conventional queries collection representation.
 *
 * Each child @ConventionalQuery would be represented as a
 * separate method in the resulting spring-data repository.
 *
 * @see {@link ConventionalQuery}
 *
 * @author Cyril A. Karpenko <self@nikelin.ru>
 */
@Target(ElementType.TYPE)
public @interface ConventionalQueries {

    public ConventionalQuery[] value() default {};

    public boolean isTransactional() default false;

}
