package com.a5000.platform.api.annotations.generators.jpa.v2;

import com.a5000.platform.api.annotations.generators.jpa.entities.Test;
import com.a5000.platform.api.annotations.generators.jpa.mojo.GenDaoMojo;
import com.a5000.platform.api.annotations.generators.jpa.mojo.GenDtoMojo;
import com.a5000.platform.api.annotations.generators.jpa.mojo.GenJpaToDtoConverterMojo;
import com.a5000.platform.api.annotations.generators.jpa.utils.Commons;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by cyril on 8/28/13.
 */
public class GenDaoMojoTest extends AbstractMojoTestCase {

    private static final Logger log = Logger.getLogger(GenDaoMojoTest.class.getCanonicalName());

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @throws Exception
     */
    @org.junit.Test
    public void testGenDaoGoal() throws Exception
    {
        Xpp3Dom configuration = createConfiguration();
        MavenProject project = createProject( configuration );

        MojoExecution mojoExecution = newMojoExecution("gen-dao");
        mojoExecution.setConfiguration( configuration );

        GenDaoMojo mojo = (GenDaoMojo) lookupConfiguredMojo(newMavenSession(project), mojoExecution);
        mojo.setProject(project);
        mojo.execute();
        assertNotNull( mojo );

        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File("target/"));
        JavaClass[] classes = builder.getClasses();

        JavaClass testDaoClass = builder.getClassByName("com.a5000.platform.api.annotations.generators.jpa.entities.ITestDAO");
        assertClassMethod( builder, testDaoClass, "findByName", new ParameterMatcher[] { new ParameterMatcher(String.class) },
                new TypesMatcher(Test.class), new AnnotationMatcher[] {} );
        assertClassMethod( builder, testDaoClass, "findByUserId", new ParameterMatcher[] { new ParameterMatcher(Long.class) }, new TypesMatcher(List.class),
                new AnnotationMatcher[] {} );
        assertClassMethod( builder, testDaoClass, "deleteWhereUserIdIs", new ParameterMatcher[] { new ParameterMatcher(Long.class) }, new TypesMatcher(void.class), new AnnotationMatcher[] {
                new AnnotationMatcher(GenDaoMojo.TRANSACTIONAL_ANNOTATION_CLASS_NAME),
                new AnnotationMatcher(GenDaoMojo.MODIFYING_ANNOTATION_CLASS_NAME)
        } );

        assertClassMethod(builder, testDaoClass, "deleteWhereUserIdIn", new ParameterMatcher[] {
                new ParameterMatcher(Long[].class)
        }, new TypesMatcher(void.class), new Matcher[]{
                new AnnotationMatcher(GenDaoMojo.TRANSACTIONAL_ANNOTATION_CLASS_NAME, Commons.<String, String>map() ),
                new AnnotationMatcher(GenDaoMojo.MODIFYING_ANNOTATION_CLASS_NAME, Commons.<String, String>map())
        });

    }

    private static void assertClassMethod( JavaDocBuilder builder,
                                           JavaClass clazz,
                                           String methodName,
                                           Matcher<JavaParameter>[] parameters,
                                           Matcher<JavaClass> returnType,
                                           Matcher<Annotation>[] annotationsExpected ) {
        JavaMethod javaMethod = null;
        boolean found = false;
        for ( JavaMethod method : clazz.getMethods() ) {
            if ( !method.getName().equals(methodName) || method.getParameters().length != parameters.length ) {
                continue;
            }

            for ( int i = 0; i < method.getParameters().length; i++ ) {
                MatchResult parameterMatch = parameters[i].match(method.getParameters()[i]);
                found = parameterMatch.isResult();
                if ( !found ) {
                    log.severe("Failed to match parameter " + method.getParameters()[i].getName()
                            + " due to the next errors: " + parameterMatch.getMessage());
                    break;
                }
            }

            if ( method.getParameters().length == 0 && parameters.length == 0 ) {
                found = true;
            }

            if ( found ) {
                javaMethod = method;
            }
        }

        assertTrue("Method " + methodName + " not found by the given signature", found);
        assertTrue("Unexpected return type: " + javaMethod.getReturnType().getJavaClass().getFullyQualifiedName(),
                returnType.match(javaMethod.getReturnType().getJavaClass()).isResult());

        List<String> notResolved = new ArrayList<String>();
        for ( int i = 0; i < annotationsExpected.length; i++ ) {
            boolean resolved = false;
            for ( Annotation annotation : javaMethod.getAnnotations() ) {
                MatchResult resolveResult = annotationsExpected[i].match(annotation);
                if ( resolveResult.isResult() ) {
                    resolved = true;
                }
            }

            if ( !resolved ) {
                notResolved.add( ( (AnnotationMatcher) annotationsExpected[i]).annotationName );
            }
        }

        assertTrue("Some annotations not resolved: " + notResolved.toString(),
                notResolved.isEmpty() );
    }

    @org.junit.Test
    public void testGenDtoGoal() throws Exception {
        Xpp3Dom configuration = createConfiguration();
        MavenProject project = createProject( configuration );

        MojoExecution mojoExecution = newMojoExecution("gen-dto");
        mojoExecution.setConfiguration( configuration );

        GenDtoMojo mojo = (GenDtoMojo) lookupConfiguredMojo(newMavenSession(project), mojoExecution);
        mojo.setProject(project);
        mojo.execute();
        assertNotNull( mojo );

        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File("target/"));
        JavaClass testDtoClass = builder.getClassByName(Test.class.getCanonicalName() + "DTO");
        assertNotNull(testDtoClass);
        assertClassMethod(builder, testDtoClass, "getRelatedTestAId", new ParameterMatcher[] {}, new TypesMatcher(Long.class),
                new AnnotationMatcher[] {} );
        assertClassMethod(builder, testDtoClass, "setRelatedTestAId", new ParameterMatcher[] { new ParameterMatcher(Long.class) },
                new TypesMatcher(void.class), new AnnotationMatcher[] {} );

        assertClassMethod(builder, testDtoClass, "getRelatedTests", new ParameterMatcher[] {}, new TypesMatcher(List.class),
                new AnnotationMatcher[] {} );
    }

    public void testJpaToDto() throws Exception {
        Xpp3Dom configuration = createConfiguration();

        Xpp3Dom convertersPackage = new Xpp3Dom("convertersPackage");
        convertersPackage.setValue("com.redshape.generators.jpa.services");
        configuration.addChild(convertersPackage);

        MavenProject project = createProject( configuration );

        MojoExecution mojoExecution = newMojoExecution("gen-jpa-converter");
        mojoExecution.setConfiguration(configuration);

        GenJpaToDtoConverterMojo mojo = (GenJpaToDtoConverterMojo)
                lookupConfiguredMojo(newMavenSession(project), mojoExecution);
        mojo.setProject(project);
        mojo.execute();
        assertNotNull( mojo );

        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File("target/"));
        JavaClass[] classes = builder.getClasses();
        assertFalse(classes.length == 0);
    }

    protected Xpp3Dom createConfiguration() {
        Xpp3Dom configuration = new Xpp3Dom("configuration");

        Xpp3Dom sourceRootNode = new Xpp3Dom("sourceRoot");
        sourceRootNode.setValue("src/test/java");
        configuration.addChild( sourceRootNode );

        Xpp3Dom basePackageNode = new Xpp3Dom("basePackage");
        basePackageNode.setValue("com.redshape.generators.jpa.entities");
        configuration.addChild( basePackageNode );

        Xpp3Dom daoPackageNode = new Xpp3Dom("daoPackage");
        daoPackageNode.setValue("com.redshape.generators.jpa");
        configuration.addChild( daoPackageNode );

        Xpp3Dom dtoPackageNode = new Xpp3Dom("dtoPackage");
        dtoPackageNode.setValue("com.redshape.generators.jpa");
        configuration.addChild( dtoPackageNode );

        Xpp3Dom attachSuffixes = new Xpp3Dom("attachSuffixes");
        attachSuffixes.setValue("true");
        configuration.addChild( attachSuffixes );

        Xpp3Dom attachPrefixes = new Xpp3Dom("attachPrefixes");
        attachPrefixes.setValue("true");
        configuration.addChild( attachPrefixes );

        Xpp3Dom attachPostfixes = new Xpp3Dom("attachPostfixes");
        attachPostfixes.setValue("true");
        configuration.addChild( attachPostfixes );

        Xpp3Dom attachAffixes = new Xpp3Dom("disableAffixesAttach");
        attachAffixes.setValue("false");
        configuration.addChild( attachAffixes );

        Xpp3Dom entityPatternNode = new Xpp3Dom("entityPattern");
        entityPatternNode.setValue("**/*.java");
        configuration.addChild(entityPatternNode);

        return configuration;
    }

    protected MavenProject createProject( Xpp3Dom configuration ) {
        MavenProjectStub project = new MavenProjectStub();
        project.setGroupId("test-mojo");
        project.setArtifactId("test-mojo");
        project.setCompileSourceRoots(Commons.list("src/test/java"));

        Plugin generatorPlugin = new Plugin();
        generatorPlugin.setGroupId("com.redshape.utils.generation-kit");
        generatorPlugin.setArtifactId("spring-data-generator");
        generatorPlugin.setVersion("1.0.23-SNAPSHOT");
        generatorPlugin.setConfiguration( configuration );

        Build build = new Build();
        build.addPlugin(generatorPlugin);
        project.setBuild(build);

        return project;
    }

    public class MatchResult {
        private final boolean result;
        private final String message;

        public MatchResult(boolean result) {
            this(result, null);
        }

        public MatchResult(boolean result, String message) {
            this.result = result;
            this.message = message;
        }

        public boolean isResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }

    public interface Matcher<T> {

        public MatchResult match(T object);

    }

    public class ParameterMatcher implements Matcher<JavaParameter> {

        private final Class<?> parameterType;
        private final AnnotationMatcher[] annotationMatchers;

        public ParameterMatcher(Class<?> parameterType) {
            this(parameterType, new AnnotationMatcher[] {} );
        }

        public ParameterMatcher(Class<?> parameterType, AnnotationMatcher[] annotationMatchers) {
            this.parameterType = parameterType;
            this.annotationMatchers = annotationMatchers;
        }

        @Override
        public MatchResult match(JavaParameter object) {
            if ( !parameterType.getCanonicalName().equals(object.getType().isArray() ?
                    object.getType().getFullyQualifiedName() + "[]" : object.getType().getFullyQualifiedName()) ) {
                return new MatchResult(false, "Wrong parameter " + object.getName() + " type: "
                        + object.getType().getFullyQualifiedName() + "; expected: " + parameterType.getCanonicalName() );
            }

            if ( annotationMatchers.length > 0 ) {
                if (object.getAnnotations().length != annotationMatchers.length) {
                    return new MatchResult(false, "Some of expected annotations not presents on parameter '" + object.getName() + "'");
                }

                for (int i = 0; i < object.getAnnotations().length; i++) {
                    if (!annotationMatchers[i].match(object.getAnnotations()[i]).isResult()) {
                        return new MatchResult(false, "Failed to match annotation " + annotationMatchers[i].annotationName
                                + " on parameter " + object.getName());
                    }
                }
            }

            return new MatchResult(true);
        }
    }

    public class TypesMatcher implements Matcher<JavaClass> {

        private final Class<?> targetClass;

        public TypesMatcher(Class<?> targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        public MatchResult match(JavaClass object) {
            return new MatchResult(object.getFullyQualifiedName().equals( targetClass.getCanonicalName() ),
                    "Wrong type " + object.getFullyQualifiedName() + "; expected: " + targetClass.getCanonicalName() );
        }
    }

    public class AnnotationMatcher implements Matcher<Annotation> {

        private final String annotationName;
        private final Map parameters;

        public AnnotationMatcher( String annotationName ) {
            this(annotationName, new HashMap());
        }

        public AnnotationMatcher( String annotationName, Map parameters ) {
            this.annotationName = annotationName;
            this.parameters = parameters;
        }

        @Override
        public MatchResult match(Annotation object) {
            if ( !(object.getType().getFullyQualifiedName().equals(annotationName)) ) {
                return new MatchResult(false, "Wrong annotation type " + object.getType().getFullyQualifiedName() + "; expected: " + annotationName );
            }

            Set<Map.Entry> entrySet = object.getPropertyMap().entrySet();
            boolean result = true;
            for ( Map.Entry entryItem : entrySet ) {
                result = result && parameters.containsKey(entryItem.getKey());
                result = result && parameters.get(entryItem.getKey()) != null;
                result = result && parameters.get(entryItem.getKey()).equals( entryItem.getValue() );
            }

            return new MatchResult(result, "Annotation parameters do not match");
        }
    }

}
