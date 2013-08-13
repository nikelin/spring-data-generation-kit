package com.redshape.generators.annotations.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation to mark domain object fields which
 * must be ignored during DTO model composition.
 *
 * @author Cyril A. Karpenko
 */
@Target(ElementType.FIELD)
public @interface DtoExclude {

}
