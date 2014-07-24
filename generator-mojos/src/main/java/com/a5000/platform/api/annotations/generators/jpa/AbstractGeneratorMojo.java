package com.a5000.platform.api.annotations.generators.jpa;

import com.a5000.platform.api.annotations.generators.jpa.utils.StringUtils;
import com.sun.codemodel.*;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public abstract class AbstractGeneratorMojo extends AbstractMojo {

    public static final String DTO_GENERATOR_PREFIX = "";
    public static final String DTO_GENERATOR_SUFFIX = "dto";
    public static final String DTO_GENERATOR_POSTFIX = "DTO";

    public static final String DAO_GENERATOR_PREFIX = "I";
    public static final String DAO_GENERATOR_SUFFIX = "dao";
    public static final String DAO_GENERATOR_POSTFIX = "DAO";

    public static final String ENTITY_ANNOTATION_CLASS_NAME
            = "javax.persistence.Entity";
    public static final String SERVICE_ANNOTATION_CLASS_NAME
            = "org.springframework.stereotype.Service";
    private static final String WELCOME_MESSAGE
            = "----------------------------   Generator: %s  --------------------------------------------";

    public static final String DTO_EXCLUDE_ANNOTATION_CLASS_NAME = "DtoExclude";
    public static final String DTO_INCLUDE_ANNOTATION_CLASS_NAME = "DtoInclude";
    public static final String DTO_EXTENDS_ANNOTATION_CLASS_NAME = "DtoExtend";
    public static final String DTO_METHOD_ANNOTATION_CLASS_NAME = "DtoMethod";
    private static final String MAPPED_SUPERCLASS_ANNOTATION_CLASS_NAME = "javax.persistence.MappedSuperclass";
    private static final String INHERITANCE_ANNOTATION_CLASS_NAME = "javax.persistence.Inheritance";
    private static final String BOOLEAN_TYPE_NAME = "java.lang.Boolean";
    private static final String DOUBLE_TYPE_NAME = "java.lang.Double";
    private static final String INTEGER_TYPE_NAME = "java.lang.Integer";
    private static final String BYTE_TYPE_NAME = "java.lang.Byte";
    private static final String FLOAT_TYPE_NAME = "java.lang.Float";
    private static final String LONG_TYPE_NAME = "java.lang.Long";

    protected final JCodeModel codeModel;
    protected JavaDocBuilder classMetaBuilder;

    @Parameter( property = "outputPath", required = true, defaultValue = "target/")
    protected String outputPath = "target/";

    @Parameter( property = "entityPattern", required = true )
    protected String entityPattern = "";

    @Parameter( property = "sourceRoot", defaultValue = "src/main/java" )
    protected String sourceRoot = "src/main/java";

    @Parameter( property = "basePackage", required = true)
    protected String basePackage;

    @Parameter( property = "daoPackage", required = true )
    protected String daoPackage;

    @Parameter( property = "dtoPackage", required = true )
    protected String dtoPackage;
    
    @Parameter( property = "attachSuffixes", defaultValue = "true" )
    protected Boolean attachSuffixes = true;

    @Parameter( property = "attachPrefixes", defaultValue = "true" )
    protected Boolean attachPrefixes = true;

    @Parameter( property = "attachPostfixes", defaultValue = "true" )
    protected Boolean attachPostfixes = true;

    @Parameter( property = "disableAffixesAttach", defaultValue = "false" )
    protected Boolean disableAffixesAttach = false;

    @Parameter( property = "skipStaticFields", defaultValue = "false" )
    protected Boolean skipStaticFields = false;

    @Parameter( property = "convertersPackage", required = true )
    protected String convertersPackage;

    private final AtomicBoolean classMetaBuilderCreated = new AtomicBoolean();

    private final String generatorName;

    private final String generatorPostfix;

    private final String generatorPrefix;

    private final String generatorSuffix;

    protected AbstractGeneratorMojo( String generatorName,
                                     String generatorPrefix,
                                     String generatorSuffix,
                                     String generatorPostfix) {
        super();

        this.generatorPrefix = generatorPrefix;
        this.generatorPostfix = generatorPostfix;
        this.generatorSuffix = generatorSuffix;

        this.generatorName = generatorName;
        this.codeModel = new JCodeModel();
    }

    protected JavaDocBuilder getClassMetaBuilder() {
        if ( classMetaBuilderCreated.compareAndSet(false, true) ) {
            try {
                this.classMetaBuilder = createJavaDocBuilder();
            } catch (MojoExecutionException e) {
                throw new IllegalStateException( e.getMessage(), e );
            }
        }

        return this.classMetaBuilder;
    }

    protected boolean isA( JavaClass classType, String className ) {
        return classType.getFullyQualifiedName().equals(className)
                || classType.getPackageName().isEmpty() && classType.getName().endsWith( className )
                || (classType.getFullyQualifiedName().substring(
                        classType.getFullyQualifiedName().lastIndexOf(".") + 1).equals(className) );
    }

    protected String[] findClasses( String sourceRoot, String classPattern ) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( sourceRoot );
        scanner.setIncludes( new String[] { classPattern } );

        scanner.scan();

        String[] sources = scanner.getIncludedFiles();
        if ( sources.length == 0 )
        {
            getLog().info("No source entities is suitable to be processed");
            return new String[0];
        }

        return sources;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info( String.format(WELCOME_MESSAGE, generatorName) );
        getLog().info("Looking for classes matching '" + entityPattern + "' pattern in " + sourceRoot );
        String[] classes = findClasses( sourceRoot, entityPattern );

        int processed = 0;
        for ( String className : classes ) {
            JavaClass entityClass = getClassMetaBuilder().getClassByName( pathToName(className) );
            if ( isSupported(entityClass) ) {
                getLog().info("Processing class " + entityClass.getFullyQualifiedName() );
                generateClass(entityClass);
                processed += 1;
            }
        }

        getLog().info( processed + " classes has been processed...");

        getLog().info( "Flushing code model contents..." );
        writeClasses();
    }

    protected void writeClasses() throws MojoExecutionException {
        File outputDirectory = new File(outputPath);
        outputDirectory.mkdirs();

        if ( !outputDirectory.exists() ) {
            throw new MojoExecutionException("Non-exists output path specified...");
        }

        if ( !outputDirectory.isDirectory() ) {
            throw new MojoExecutionException("Output path must be a directory type!");
        }

        try {
            codeModel.build( outputDirectory, (PrintStream) null );
        } catch ( IOException e ) {
            throw new MojoExecutionException("Failed to save code model contents...", e );
        }
    }

    private String pathToName( String path ) {
        return path.replaceAll(Pattern.quote(File.separator), ".").replace(".java", "").trim();
    }

    protected boolean hasDescendants( JavaClass clazz ) {
        boolean result = false;
        for ( JavaClass classItem : getClassMetaBuilder().getClasses() ) {
            if ( !classItem.isA( clazz )
                    || clazz.equals(classItem)
                    || classItem.getFullyQualifiedName().equals(Object.class.getCanonicalName()) ) {
                continue;
            }

            result = true;
            break;
        }

        return result;
    }

    protected List<String> resolveSyntheticFieldGetter(Annotation syntheticFieldAnnotation) {
        String expression = normalizeAnnotationValue(
                (String) syntheticFieldAnnotation.getNamedParameter("expression")
        );

        List<String> gettersStack = new ArrayList<String>();
        String[] pathParts = expression.split("\\.");
        for ( String pathPart : pathParts ) {
            gettersStack.add( generateGetterName(pathPart) );
        }

        return gettersStack;
    }

    protected boolean isResolvableSyntheticField(Annotation syntheticFieldAnnotation) {
        return syntheticFieldAnnotation != null
                && syntheticFieldAnnotation.getNamedParameter("expression") != null
                && !normalizeAnnotationValue(
                (String) syntheticFieldAnnotation.getNamedParameter("expression") ).isEmpty();
    }

    protected Annotation getSyntheticFieldMethod( JavaField field ) {
        JavaClass fieldContext = field.getParentClass();
        Annotation extendsAnnotation = null;
        while ( extendsAnnotation == null && fieldContext != null ) {
            for ( Annotation annotation : fieldContext.getAnnotations() ) {
                if ( !isA(annotation.getType().getJavaClass(),
                        DTO_EXTENDS_ANNOTATION_CLASS_NAME) ) {
                    continue;
                }

                extendsAnnotation = annotation;
                break;
            }

            fieldContext = fieldContext.getSuperJavaClass();
        }

        if ( extendsAnnotation == null ) {
            return null;
        }

        Annotation targetAnnotation = null;
        Object annotationValue = extendsAnnotation.getNamedParameter("value");
        if ( annotationValue instanceof Annotation ) {
            targetAnnotation = (Annotation) annotationValue;
        } else if ( annotationValue instanceof List) {
            for ( Annotation annotation : (List<Annotation>) annotationValue ) {
                if ( normalizeAnnotationValue(
                        (String) annotation.getNamedParameter("value") )
                        .equals( field.getName() ) ) {
                    targetAnnotation = annotation;
                    break;
                }
            }
        }

        return targetAnnotation;
    }

    protected boolean isInterface(String fullyQualifiedName) {
        return !classMetaBuilder.getClassByName( fullyQualifiedName ).isInterface();
    }

    protected boolean isJpaRelationType(JavaClass annotationTypeName) {
        return  isA( annotationTypeName, ManyToOne.class.getSimpleName())
                || isA(annotationTypeName, ManyToMany.class.getSimpleName())
                || isA( annotationTypeName, OneToMany.class.getSimpleName())
                || isA( annotationTypeName, OneToOne.class.getSimpleName());
    }

    protected boolean isSimpleType( JavaClass type ) {
        try {
            JType.parse(codeModel, type.getFullyQualifiedName());
            return true;
        } catch ( IllegalArgumentException e ) {
            return type.isEnum()
                    || type.isA( Date.class.getCanonicalName() )
                    || type.getFullyQualifiedName().startsWith("java.lang");
        }
    }

    protected boolean isDtoType( JavaClass type ) {
        String typeName = type.getFullyQualifiedName();

        if ( !disableAffixesAttach ) {
            if ( typeName.endsWith(DTO_GENERATOR_POSTFIX)
                    && typeName.startsWith(DTO_GENERATOR_PREFIX) ) {
                return true;
            }
        }

        if ( typeName.startsWith( dtoPackage ) ) {
            return true;
        }

        return false;
    }

    protected boolean isMethodExists( String methodName, String className ) {
        return isMethodExists(methodName, className, true, true);
    }

    protected boolean isMethodExists( String methodName, String className,
                                      final boolean deepSearch,
                                      final boolean publicOnly ) {
        JavaClass javaClazz = classMetaBuilder.getClassByName(className);
        if ( javaClazz == null ) {
            throw new IllegalArgumentException("Class not found: "
                    + javaClazz.getFullyQualifiedName() );
        }

        boolean result = false;
        JavaClass parentClass = javaClazz;
        do {
            for ( JavaMethod method : parentClass.getMethods() ) {
                if ( !method.getName().equals( methodName )
                        || ( publicOnly && !method.isPublic() ) ) {
                    continue;
                }

                result = true;
                break;
            }

            parentClass = parentClass.getSuperJavaClass();
        } while ( deepSearch && !result && parentClass != null );

        return result;
    }

    protected boolean isListType( JavaClass classType ) {
        return classType.isA( List.class.getCanonicalName() );
    }

    protected boolean isListType( String classType ) {
        return isListType( classMetaBuilder.getClassByName(classType) );
    }

    protected boolean isSetType( JavaClass classType ) {
        return classType.isA( Set.class.getCanonicalName() );
    }

    protected boolean isSetType( String className ) {
        return isSetType( classMetaBuilder.getClassByName(className) );
    }

    protected boolean isCollectionType( String className ) {
        return isCollectionType( classMetaBuilder.getClassByName(className) );
    }

    protected boolean isCollectionType( JavaClass classType ) {
        return classType.isA( Collection.class.getCanonicalName() );
    }

    protected boolean isMappedSuperclassEntity( JavaClass clazz ) {
        return hasAnnotation(clazz, MAPPED_SUPERCLASS_ANNOTATION_CLASS_NAME);
    }

    protected boolean isJpaEntity(JavaClass clazz) {
        return hasAnnotation(clazz, ENTITY_ANNOTATION_CLASS_NAME )
                || hasAnnotation(clazz, MAPPED_SUPERCLASS_ANNOTATION_CLASS_NAME);
    }

    protected boolean isInheritanceClass(JavaClass clazz){
        return hasAnnotation(clazz, INHERITANCE_ANNOTATION_CLASS_NAME);
    }


    protected boolean hasAnnotation(AbstractBaseJavaEntity clazz, String className ) {
        return hasAnnotation( clazz, className, false );
    }

    protected boolean hasAnnotation(AbstractBaseJavaEntity clazz, String className, boolean checkParent ) {
        boolean result = false;
        for ( Annotation annotation : clazz.getAnnotations() ) {
            if ( !isA( annotation.getType().getJavaClass(), className) ) {
                continue;
            }

            result = true;
            break;
        }

        return result;
    }

    protected JType convertType( JavaClass context, Type originalType ) {
        return convertType( context, originalType, generatorPrefix, generatorSuffix, generatorPostfix);
    }

    protected JType convertType( JavaClass context, Type originalType, String generatorPrefix,
                                 String generatorSuffix, String generatorPostfix ) {
        JClass returnType = null;
        if ( isCollectionType(originalType.getJavaClass()) ) {
            if ( originalType.getActualTypeArguments().length == 0 ) {
                returnType = codeModel.ref(originalType.getFullyQualifiedName());
            } else {
                List<JClass> narrowsList = new ArrayList<JClass>();
                for ( Type type : originalType.getActualTypeArguments() ) {
                    boolean found = false;
                    for ( TypeVariable typeVariable : context.getTypeParameters() ) {
                        if ( typeVariable.getName().equals( type.getFullyQualifiedName() ) ) {
                            narrowsList.add( codeModel.ref( typeVariable.getName() ) );
                            found = true;
                        }
                    }

                    if ( !found ) {
                        if ( isJpaEntity(type.getJavaClass()) ) {
                            narrowsList.add(
                                codeModel.ref(
                                    prepareClassName(dtoPackage, type.getFullyQualifiedName(), generatorPrefix,
                                            generatorSuffix, generatorPostfix)
                                )
                            );
                        } else {
                            narrowsList.add( codeModel.ref(type.getFullyQualifiedName()) );
                        }

                    }
                }

                returnType = codeModel.ref(originalType.getFullyQualifiedName());

                if ( narrowsList.size() == originalType.getActualTypeArguments().length ) {
                    returnType = returnType.narrow( narrowsList );
                }
            }
        } else if ( isSimpleType( originalType.getJavaClass() )
                || isDtoType(originalType.getJavaClass()) ) {
            returnType = codeModel.ref( originalType.getFullyQualifiedName() );
        } else if ( isJpaEntity( originalType.getJavaClass() ) ) {
            returnType = codeModel.ref(
                prepareClassName(dtoPackage, originalType.getFullyQualifiedName(), generatorPrefix,
                        generatorSuffix, generatorPostfix)
            );
        }

        return returnType;
    }

    protected String detectIdKeyType( JavaClass entityClass ) {
        String result = null;

        for ( JavaField field : entityClass.getFields() ) {
            boolean isId = false;
            for (Annotation annotation : field.getAnnotations()) {
                if ( !isA( annotation.getType().getJavaClass(), Id.class.getName() ) ) {
                    continue;
                }

                isId = true;
                break;
            }

            if ( isId ) {
                result = field.getType().getFullyQualifiedName();
                break;
            }
        }

        if ( result == null && entityClass.getSuperClass() != null
                && !isA( entityClass.getSuperJavaClass(), Object.class.getSimpleName() ) ) {
            result = detectIdKeyType( entityClass.getSuperJavaClass() );
        }

        return result;
    }

    protected Set<JavaField> collectAllFields( JavaClass javaClass ) {
        Set<JavaField> result = new HashSet<JavaField>();

        JavaClass parent = javaClass;
        while ( parent != null ) {
            result.addAll( Arrays.asList(parent.getFields()) );
            parent = parent.getSuperJavaClass();
        }

        return result;
    }

    protected void generateAccessors(JDefinedClass clazz, JFieldVar clazzField) {
        generateSetter( clazz, clazzField);
        generateGetter( clazz, clazzField);
    }

    protected String generateSetterName( String fieldName ) {
        return "set" + StringUtils.ucfirst(fieldName);
    }

    protected void generateSetter( JDefinedClass clazz, JFieldVar clazzField ) {
        JMethod setterMethod = clazz.method(JMod.PUBLIC, JType.parse(codeModel, "void"),
                generateSetterName(clazzField.name()) );
        JVar valueVar = setterMethod.param( clazzField.type(), "value" );
        setterMethod.body().assign( JExpr.refthis( clazzField.name() ), valueVar);
    }

    protected String generateGetterName( String name ) {
        return "get" + StringUtils.ucfirst(name);
    }

    protected void generateGetter( JDefinedClass clazz, JFieldVar clazzField ) {
        JMethod getterMethod = clazz.method(JMod.PUBLIC, clazzField.type(),
               generateGetterName(clazzField.name()) );

        JExpression returnStm = JExpr.refthis( clazzField.name() );
        if ( clazzField.type().isReference()
                && isSimpleType( classMetaBuilder.getClassByName(clazzField.type().fullName()) ) ) {
            JConditional nullHandler = getterMethod.body()._if(returnStm.eq(JExpr._null()));

            String typeName = clazzField.type().fullName();

            JExpression lit;
            if (typeName.equals(BOOLEAN_TYPE_NAME) ) {
                lit = JExpr.lit(false);
            } else if ( typeName.equals(INTEGER_TYPE_NAME) ) {
                lit = JExpr.lit(0);
            } else if ( typeName.equals(BYTE_TYPE_NAME) ) {
                lit = JExpr.lit((byte) 0);
            } else if ( typeName.equals(FLOAT_TYPE_NAME) ) {
                lit = JExpr.lit(0.0f);
            } else if ( typeName.equals(DOUBLE_TYPE_NAME) ) {
                lit = JExpr.lit(0.0d);
            } else if ( typeName.equals(LONG_TYPE_NAME) ) {
                lit = JExpr.lit(0L);
            } else {
                lit = JExpr._null();
            }

            nullHandler._then()._return( lit );
            nullHandler._else()._return( returnStm );
        } else {
            getterMethod.body()._return(returnStm);
        }
    }

    protected JDefinedClass defineInterface( String className, String generatedPackage ) throws JClassAlreadyExistsException {
        return defineInterface( className, generatedPackage, generatorPrefix, generatorSuffix, generatorPostfix );
    }

    protected JDefinedClass defineInterface( String className, String generatedPackage, String prefix,
                                             String suffix, String postfix)
            throws JClassAlreadyExistsException {
        return _define(className, generatedPackage, prefix, suffix, postfix, true, ClassType.INTERFACE);
    }

    protected JDefinedClass defineClass( String className, String generatedPackage, boolean isAbstract )
            throws JClassAlreadyExistsException {
        return _define(className, generatedPackage, generatorPrefix, generatorSuffix,
                generatorPostfix, isAbstract, ClassType.CLASS);
    }

    protected JDefinedClass defineClass( String className, String generatedPackage, String prefix,
                                         String suffix, String postfix, boolean isAbstract )
        throws JClassAlreadyExistsException {
        return _define(className, generatedPackage, prefix, suffix, postfix, isAbstract, ClassType.CLASS);
    }

    private JDefinedClass _define( String className, String generatedPackage, String prefix, String suffix, String postfix,
                                   boolean isAbstract, ClassType type)
            throws JClassAlreadyExistsException {
        String preparedClassName = prepareClassName( generatedPackage, className, prefix, suffix, postfix);
        int bIndex = preparedClassName.lastIndexOf( "." );
        String packagePart = preparedClassName.substring( 0, bIndex );
        String classPart = preparedClassName.substring( bIndex + 1 );

        int flags = JMod.PUBLIC;
        if ( isAbstract && type.equals(ClassType.CLASS) ) {
            flags |= JMod.ABSTRACT;
        }

        return codeModel._package( packagePart )
                ._class(flags,  classPart, type);
    }

    protected String prepareClassName( String generatedPackage, String name ) {
        try {
            return prepareClassName(generatedPackage, name, generatorPrefix, generatorSuffix, generatorPostfix);
        } catch ( Throwable e ) {
            throw new IllegalStateException("Failed to prepare class name: " + generatedPackage + "/" + name, e );
        }
    }

    protected String prepareClassName( String generatedPackage, String name,
                                       String prefix, String suffix, String postfix) {
        name = name.replace(basePackage, generatedPackage);

        if ( suffix != null && !disableAffixesAttach && attachSuffixes ) {
            name = name.replace(generatedPackage, generatedPackage + "." + suffix);
        }

        if ( prefix != null && !disableAffixesAttach && attachPrefixes ) {
            int bIndex = name.lastIndexOf(".");
            name = name.substring(0, bIndex ) + "." + prefix + name.substring( bIndex + 1 );
        }

        if ( postfix != null && !disableAffixesAttach && attachPostfixes ) {
            name = name + postfix;
        }

        return name;
    }

    protected abstract boolean isSupported( JavaClass entityClass );

    protected abstract void generateClass( JavaClass entityClass ) throws MojoExecutionException;

}
