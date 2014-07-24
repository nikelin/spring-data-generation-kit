package com.a5000.platform.api.annotations.generators.jpa.mojo;

import com.a5000.platform.api.annotations.generators.jpa.AbstractGeneratorMojo;
import com.sun.codemodel.*;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by cyril on 8/28/13.
 */
@Mojo( name = "gen-jpa-converter", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true)
public class GenJpaToDtoConverterMojo extends AbstractGeneratorMojo {

    private static final String CONVERTER_CLASS_NAME = "DtoConversionService";
    private static final String CONVERTER_METHOD_NAME = "convertToDto";
    private static final String DTO_INCLUDE_ANNOTATION_CLASS_NAME = "DtoInclude";
    private static final String DTO_EXCLUDE_ANNOTATION_CLASS_NAME = "DtoExclude";
    private static final String METHODS_CACHE_FIELD_NAME = "METHODS";
    private static final String CONVERSATION_METHOD_NOT_FOUND_EXCEPTION = "Conversion method not found: %s";
    private static final String LIST_CONVERTER_METHOD_NAME = "convertToDtoList";
    private static final String CONVERT_TO_IDS_LIST_METHOD_NAME = "convertToIdsList";
    private static final String ISTORED_BEAN_CLASS_NAME = "com.a5000.platform.api.model.domain.api.IStoredBean";
    private static final String A5_READONLY_TRANSACTIONAL_ANNOTATION_CLASS = "com.a5000.platform.api.services.A5TransactionalReadOnly";

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private JDefinedClass converterClazz;
    private JFieldVar cacheField;

    public GenJpaToDtoConverterMojo() {
        super("JPA to DTO conversion services generator", "", "", "");
    }

    protected void init() throws JClassAlreadyExistsException {
        if ( !initialized.compareAndSet(false, true) ) {
            return;
        }

        this.converterClazz = codeModel._package(convertersPackage)
                ._class( JMod.PUBLIC, CONVERTER_CLASS_NAME );
        this.converterClazz.annotate( codeModel.ref(SERVICE_ANNOTATION_CLASS_NAME) )
                .param("value", "DtoConversionServiceImpl");
    }
    @Override
    protected void generateClass(JavaClass entityClazz) throws MojoExecutionException {
        if ( entityClazz.isAbstract() ) {
            getLog().info("Skipping abstract class " + entityClazz.getFullyQualifiedName() );
            return;
        }

        generateConverter(entityClazz);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            init();
            generateConvertToIdsListMethod(converterClazz);
            generateTemplateConvertMethod(converterClazz);
            generateTemplateListConvertMethod(converterClazz);

            super.execute();
        } catch (JClassAlreadyExistsException e) {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    protected void generateConvertToIdsListMethod( JDefinedClass converterClazz ) {
        JMethod converterMethod = converterClazz.method(JMod.PRIVATE | JMod.STATIC,
                codeModel.ref(List.class).narrow(Long.class),
                CONVERT_TO_IDS_LIST_METHOD_NAME );
        JClass entityTypeRef = codeModel.ref(ISTORED_BEAN_CLASS_NAME);
        JClass listTypeRef = codeModel.ref(List.class);
        JVar inputArg = converterMethod.param( listTypeRef.narrow( entityTypeRef.wildcard() ), "arg" );
        JBlock block = converterMethod.body();

        JVar resultVar = block.decl( listTypeRef.narrow(Long.class), "result",
                JExpr._new( codeModel.ref(ArrayList.class).narrow(Long.class) ) );
        block._if( JOp.eq( inputArg, JExpr._null() ) )
             ._then()._return(resultVar);

        JBlock foreachBlock = block.forEach( entityTypeRef, "record", inputArg )
             .body();
        foreachBlock._if( JOp.eq( JExpr._null(), JExpr.ref("record") ) )
            ._then()
                ._continue();
        foreachBlock.invoke( resultVar, "add").arg( JExpr.ref("record").invoke("getId") ) ;

        block._return(resultVar);
    }

    protected void generateConverter( JavaClass entityClazz ) {
        JClass entityClazzModel = codeModel.ref( entityClazz.getFullyQualifiedName() );
        JClass dtoRef = codeModel.ref(
            prepareClassName(
                dtoPackage,
                entityClazz.getFullyQualifiedName(),
                DTO_GENERATOR_PREFIX,
                DTO_GENERATOR_SUFFIX,
                DTO_GENERATOR_POSTFIX
            )
        );

        JMethod converterMethod = converterClazz.method(JMod.PUBLIC,
                codeModel.ref(prepareClassName(dtoPackage, entityClazz.getFullyQualifiedName(),
                        DTO_GENERATOR_PREFIX, DTO_GENERATOR_SUFFIX, DTO_GENERATOR_POSTFIX)),
                CONVERTER_METHOD_NAME);
        JVar converterMethodParam = converterMethod.param( entityClazzModel, "value" );
        JBlock block = converterMethod.body();

        block._if( converterMethodParam.eq( JExpr._null() ) )
                ._then()._return( JExpr._null() );

        JVar dtoInstance = block.decl( dtoRef, "result", JExpr._new(dtoRef) );

        Set<CollectedJavaField> collectedFields = collectConvertibleFields(entityClazz);
        for ( CollectedJavaField collectedField : collectedFields ) {
            JavaField field = collectedField.field;

            JExpression valueExpr;
            String fieldName;
            String getterName;
            String setterName;
            JClass resultType;
            if ( collectedField.isConvertible && !collectedField.isSynthetic ) {
                String aggregationType = "AggregationType.ID";
                for ( Annotation annotation : field.getAnnotations() ) {
                    if ( !isA(annotation.getType().getJavaClass(), DTO_INCLUDE_ANNOTATION_CLASS_NAME ) ) {
                        continue;
                    }

                    if ( annotation.getNamedParameter("value") != null ) {
                        aggregationType = normalizeAnnotationValue((String) annotation.getNamedParameter("value"));
                    }
                }

                boolean isList = false;
                if ("AggregationType.DTO".equals(aggregationType)) {
                    fieldName = field.getName();

                    if (isCollectionType(field.getType().getJavaClass())) {
                        if (isListType(field.getType().getJavaClass())) {
                            resultType = codeModel.ref(List.class);
                        } else if (isSetType(field.getType().getJavaClass())) {
                            resultType = codeModel.ref(Set.class);
                        } else {
                            resultType = codeModel.ref(Collection.class);
                        }

                        resultType = resultType.narrow(
                                codeModel.ref(
                                        prepareClassName(dtoPackage,
                                                field.getType().getActualTypeArguments()[0].getFullyQualifiedName(),
                                                DTO_GENERATOR_PREFIX, DTO_GENERATOR_SUFFIX, DTO_GENERATOR_POSTFIX)
                                )
                        );
                    } else {
                        resultType = codeModel.ref(
                                prepareClassName(dtoPackage, field.getType().getFullyQualifiedName(),
                                        DTO_GENERATOR_PREFIX, DTO_GENERATOR_SUFFIX, DTO_GENERATOR_POSTFIX)
                        );
                    }

                    valueExpr = JExpr.cast(
                            resultType,
                            JExpr._this().invoke(CONVERTER_METHOD_NAME).arg(
                                    converterMethodParam.invoke(getterName = generateGetterName(field.getName()))
                            )
                    );
                    break;
                } else if ( "AggregationType.ENUM".equals(aggregationType) ) {
                    fieldName = field.getName();
                    getterName = generateGetterName(field.getName());
                    if (isCollectionType(field.getType().getJavaClass())) {
                        if (isListType(field.getType().getJavaClass())) {
                            resultType = codeModel.ref(List.class);
                        } else if (isSetType(field.getType().getJavaClass())) {
                            resultType = codeModel.ref(Set.class);
                        } else {
                            resultType = codeModel.ref(Collection.class);
                        }
                    } else {
                        resultType = codeModel.ref(field.getType().getFullyQualifiedName());
                    }
                    valueExpr = JOp.cond(
                            JOp.not(converterMethodParam.invoke(getterName).eq(JExpr._null())),
                            JExpr._new(codeModel.ref(ArrayList.class)).arg(converterMethodParam.invoke(getterName)),
                            JExpr._null());
                    break;
                } else {
                        fieldName = field.getName() + "Id";

                        if ( field.getType().getJavaClass().isA(List.class.getCanonicalName()) ) {
                            resultType = codeModel.ref(List.class).narrow(Long.class);
                            isList = true;
                        } else {
                            resultType = codeModel.ref(Long.class);
                        }

                        JInvocation valueAccessInvocation =
                                converterMethodParam.invoke(getterName = generateGetterName(field.getName()));
                        valueExpr = !isList ?
                            JOp.cond(
                                JOp.not(valueAccessInvocation.eq(JExpr._null())),
                                valueAccessInvocation.invoke(generateGetterName("id")),
                                JExpr._null()
                            ) :
                            JExpr._this().invoke(CONVERT_TO_IDS_LIST_METHOD_NAME).arg( valueAccessInvocation );

                        break;
                }
            } else  {
                resultType = codeModel.ref( field.getType().getFullyQualifiedName() );
                fieldName = field.getName();
                if ( !collectedField.isSynthetic ) {
                    valueExpr = converterMethodParam.invoke( getterName = generateGetterName(fieldName) );
                } else {
                    Annotation syntheticFieldAnnotation = getSyntheticFieldMethod(field);
                    List<String> getters = null;
                    if ( isResolvableSyntheticField(syntheticFieldAnnotation) ) {
                        getters = resolveSyntheticFieldGetter(syntheticFieldAnnotation);
                    } else {
                        getLog().warn("Skipping synthetic field  " + fieldName
                                + " of " + entityClazz.getFullyQualifiedName()
                                + " which is not resolvable...");
                        continue;
                    }

                    valueExpr = converterMethodParam;

                    int i = 0;
                    JExpression parentExpr = null;
                    for ( String methodName : getters ) {
                        valueExpr = valueExpr.invoke(methodName);

                        if ( i++ != getters.size() - 1 ) {
                            parentExpr = valueExpr;
                        }
                    }

                    valueExpr = JOp.cond( JOp.not( parentExpr.eq( JExpr._null() ) ),
                            valueExpr,
                            JExpr._null() );

                    getterName = null;
                }
            }

            setterName = generateSetterName(fieldName);
            if ( !collectedField.isSynthetic
                    && ( !isMethodExists( setterName, dtoRef.fullName() ) ||
                    !isMethodExists(getterName, entityClazz.getFullyQualifiedName()) )  ) {
                getLog().warn("Skipping field " + fieldName
                        + " of " + entityClazz.getFullyQualifiedName()
                        + " with no setter and/or getter '" + getterName + "'...");
                continue;
            }

            JInvocation setterInvocation = dtoInstance.invoke( setterName );
            if ( collectedField.isConvertible ) {
                JVar convertedValueVar = block.decl( resultType, fieldName + "Converted" )
                        .init( valueExpr );

                block._if( JOp.not( convertedValueVar.eq(JExpr._null()) ) )
                        ._then().add( setterInvocation.arg(convertedValueVar) );
            } else {
                block.add( setterInvocation.arg( valueExpr ) );
            }
        }

        block._return(dtoInstance);
    }

    /**
     * @param converterClazz
     */
    protected void generateTemplateListConvertMethod( JDefinedClass converterClazz ) {
        JMethod method = converterClazz.method(
            JMod.PUBLIC,
            codeModel.ref(List.class).narrow( codeModel.ref("T") ),
            LIST_CONVERTER_METHOD_NAME
        );

        method.annotate(codeModel.ref(A5_READONLY_TRANSACTIONAL_ANNOTATION_CLASS));

        JTypeVar returnType = method.generify("T");
        JTypeVar methodParamType = method.generify("V");

        JVar methodParam =
                method.param( codeModel.ref(Collection.class).narrow(methodParamType),
                        "records" );

        JBlock methodBody = method.body();
        methodBody._if( methodParam.eq( JExpr._null() ) )
                ._then()
                    ._throw(
                        JExpr._new( codeModel.ref( IllegalStateException.class ) )
                        .arg("<null>") );

        JVar result = methodBody.decl( codeModel.ref(List.class), "result")
                .init( JExpr._new( codeModel.ref(ArrayList.class) ) );
        JForEach recordsIterator = methodBody.forEach(
                codeModel.ref(Object.class), "record", methodParam );
        recordsIterator.body()
                ._if( recordsIterator.var().eq( JExpr._null() ) )
                    ._then()
                        ._continue();
        recordsIterator.body()
            .invoke( result, "add" )
                .arg(
                    JExpr._this()
                         .invoke(CONVERTER_METHOD_NAME)
                         .arg( recordsIterator.var() )
                );

        methodBody._return(
            JExpr.cast(
                codeModel.ref(List.class).narrow(returnType),
                result
            )
        );
    }

    protected void generateTemplateConvertMethod( JDefinedClass converterClazz ) {
        defineConverterMethodsCache(converterClazz);

        JMethod method = converterClazz.method(JMod.PUBLIC, codeModel.ref("T"), "convertToDto");
        JTypeVar typeVar = method.generify("T");

        JVar methodParam = method.param( codeModel.ref(Object.class), "value" );

        method.body()._if( methodParam.eq( JExpr._null() ) )
            ._then()
                ._return( JExpr._null() );

        JType collectionType = codeModel.ref(Collection.class);

        method.body()._if( methodParam._instanceof( collectionType ) )
            ._then()
                ._return(
                    JExpr.cast( typeVar,
                        JExpr._this().invoke("convertToDtoList")
                            .arg(JExpr.cast(collectionType, methodParam)) )
                );

        JVar methodDeclaration = method.body().decl(
                codeModel.ref(Method.class),
                "method",
                cacheField.invoke("get").arg( methodParam.invoke("getClass") ) );
        method.body()._if(
            methodDeclaration.eq( JExpr._null() ) )
                ._then()._throw(
                    JExpr._new( codeModel.ref(IllegalStateException.class) )
                        .arg( String.format(CONVERSATION_METHOD_NOT_FOUND_EXCEPTION,
                                methodParam.invoke("getClass").invoke("getCanonicalName")) )
                );

        JTryBlock convertBlock = method.body()._try();
        convertBlock.body()
                ._return(
                    JExpr.cast(
                            typeVar,
                            methodDeclaration.invoke("invoke")
                                    .arg(JExpr._this())
                                    .arg(JExpr.newArray(codeModel.ref(Object.class)).add(methodParam))
                    )
                );

        JCatchBlock covertBlockCatch = convertBlock._catch( codeModel.ref(Exception.class) );
        JVar param = covertBlockCatch.param("e");
        covertBlockCatch.body()
            ._throw(
                JExpr._new( codeModel.ref(IllegalStateException.class) )
                    .arg( param.invoke("getMessage") )
                    .arg( param )
            );

    }

    protected void defineConverterMethodsCache( JDefinedClass converterClazz ) {
        JClass methodType = codeModel.ref(Method.class);
        this.cacheField = converterClazz.field(JMod.STATIC | JMod.FINAL | JMod.PRIVATE,
            codeModel.ref(Map.class).narrow(codeModel.ref(Class.class), methodType),
            METHODS_CACHE_FIELD_NAME,
            JExpr._new(
                codeModel.ref(HashMap.class).narrow(codeModel.ref(Class.class), codeModel.ref(Method.class))
            )
        );

        JForEach initLoop = converterClazz.init()
                .forEach( methodType, "method", converterClazz.staticRef("class").invoke("getMethods") );
        initLoop.body()
            ._if(initLoop.var().invoke("equals").arg(CONVERTER_METHOD_NAME))
                ._then()._continue();

        JInvocation parameterTypes = initLoop.var().invoke("getParameterTypes");
        initLoop.body()
            ._if(
                    JOp.cor(
                            parameterTypes.ref("length").eq(JExpr.lit(0)),
                            JOp.eq(
                                    parameterTypes.component(JExpr.lit(0)),
                                    codeModel.ref(Object.class).staticRef("class")
                            )
                    )
            )
                ._then()._continue();

        initLoop.body()
            .invoke( cacheField, "put")
                .arg( parameterTypes.component( JExpr.lit(0) ) )
                .arg( initLoop.var() );
    }

    protected boolean isConvertibleField( JavaField field ) {
        boolean isComplex = !this.isSimpleType(field.getType().getJavaClass());
        if ( !isComplex ) {
            return false;
        }

        boolean isIncluded = false;
        boolean isExcluded = false;
        for ( Annotation annotation : field.getAnnotations() ) {
            if ( isA(annotation.getType().getJavaClass(), DTO_INCLUDE_ANNOTATION_CLASS_NAME) ) {
                isIncluded = true;
                break;
            } else if ( isA( annotation.getType().getJavaClass(), DTO_EXCLUDE_ANNOTATION_CLASS_NAME ) ) {
                isExcluded = true;
                break;
            }
        }

        return isComplex && isIncluded && !isExcluded;
    }

    @Override
    protected boolean isSupported(JavaClass entityClass) {
        return isJpaEntity(entityClass);
    }

    protected Set<CollectedJavaField> collectConvertibleFields(JavaClass javaClass) {
        Set<JavaField> fields = super.collectAllFields(javaClass);
        Set<CollectedJavaField> result = new HashSet<CollectedJavaField>();
        for ( JavaField field : fields ) {
            if ( field.isStatic() && skipStaticFields ) {
                continue;
            }

            boolean isConvertible = isConvertibleField(field);
            if ( !isConvertible && !isSimpleType(field.getType().getJavaClass()) ) {
                continue;
            }

            result.add( new CollectedJavaField(false, isConvertible, field) );
        }

        result.addAll( collectSyntheticFields(javaClass) );

        return result;
    }

    protected Collection<CollectedJavaField> collectSyntheticFields( JavaClass javaClass ) {
        Collection<CollectedJavaField> result = new HashSet<CollectedJavaField>();

        JavaClass parent = javaClass;
        while ( parent != null ) {
            for ( Annotation annotation : parent.getAnnotations() ) {
                if ( !isA(annotation.getType().getJavaClass(),
                        DTO_EXTENDS_ANNOTATION_CLASS_NAME) ) {
                    continue;
                }

                Object value = annotation.getNamedParameter("value");
                if ( value instanceof List) {
                    for ( Annotation paramAnnotation : (List<Annotation>) value ) {
                        JavaField field = new JavaField(
                            classMetaBuilder.getClassByName(
                                normalizeAnnotationValue( (String) paramAnnotation.getNamedParameter("type") )
                                    .replace(".class", "")
                            ).asType(),
                            normalizeAnnotationValue( (String) paramAnnotation.getNamedParameter("value") )
                        );

                        field.setParentClass(javaClass);

                        result.add(
                            new CollectedJavaField(true, true, field)
                        );
                    }
                } else {
                    JavaField field = new JavaField(
                        classMetaBuilder.getClassByName(
                                normalizeAnnotationValue(
                                        (String) ( (Annotation) value).getNamedParameter("type")
                                )
                        ).asType(),
                        normalizeAnnotationValue(
                                (String) ( (Annotation) value ).getNamedParameter("value")
                        )
                    );

                    field.setParentClass(javaClass);

                    result.add(
                        new CollectedJavaField(true, true, field)
                    );
                }
            }

            parent = parent.getSuperJavaClass();
        }

        return result;
    }

    class CollectedJavaField {
        final boolean isSynthetic;
        final boolean isConvertible;
        final JavaField field;

        CollectedJavaField(boolean isSynthetic, boolean convertible, JavaField field) {
            this.isSynthetic = isSynthetic;
            this.isConvertible = convertible;
            this.field = field;
        }
    }
}
