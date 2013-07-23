package com.redshape.generators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation which can be used to add new querying method into
 * the resulting repository for a mappable class.
 *
 * The conventional query logic traits by spring based on name of its.
 *
 * For example next annotation instance which belongs to the some class Example:
 * @example
 * {@code
 *  @ConventionalQuery( name = "findByName", parameters = {
 *    @Parameter( value = "name", value = String.class )
 *  })
 * }
 *
 * would be transformed into the method in the some ExampleRepository like next:
 * {@code
 *      public interface ExampleRepository extends JpaRepository<Example, Long> {
 *
 *          public Collection<Example> findByName( @Param("name") String name );
 *
 *      }
 * }
 *
 * More about Spring Data conventions on repository methods naming you can read
 * by this link: {@linkplain http://static.springsource.org/spring-data/commons/docs/current/reference/html/}
 *
 */
@Target(ElementType.TYPE)
public @interface ConventionalQuery {

    /**
     * Name of method needs to be generated
     * @return
     */
    public String name();

    /**
     * Type of execution result; if {@link ConventionalQuery#isCollection()} has been set as true,
     * the {@link ConventionalQuery#resultType()} would be used as a generic parameter value under a
     * list type.
     *
     * @return
     */
    public Class<?> resultType() default Object.class;

    /**
     * If turned on, also additional method adds to resulting repository,
     * which return as a result {@link com.redshape.generators.annotations.paging.PageResponse<T>} where
     * T would be initialized with a value of {@link ConventionalQuery#resultType()}
     * @return
     */
    public boolean isPageable() default false;

    /**
     * Based on value of this field generator decide which result type to choose. If the value is {@literal false},
     * then plain {@link ConventionalQuery#resultType()} would be used; otherwise {@link java.util.List} with a
     * {@link ConventionalQuery#resultType()} as a generic parameter.
     * @return
     */
    public boolean isCollection() default true;

    /**
     * Detect is this conventional query requiring new transaction open to be correctly executed
     * @return
     */
    public boolean isTransactional();

    /**
     * In a cases when query processing requires input parameters, their can be given as a value of this
     * field.
     *
     * @return
     */
    public Parameter[] parameters() default {};

}
