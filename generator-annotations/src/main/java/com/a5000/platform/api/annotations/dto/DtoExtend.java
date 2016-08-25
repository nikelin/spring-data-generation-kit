package com.a5000.platform.api.annotations.dto;


import com.a5000.platform.api.annotations.generators.Parameter;

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
