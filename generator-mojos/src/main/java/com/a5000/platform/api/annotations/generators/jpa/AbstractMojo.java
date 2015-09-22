package com.a5000.platform.api.annotations.generators.jpa;

import com.a5000.platform.api.annotations.generators.jpa.utils.StringUtils;
import com.thoughtworks.qdox.JavaDocBuilder;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Created by cyril on 8/28/13.
 */
public abstract class AbstractMojo extends org.apache.maven.plugin.AbstractMojo {

    @Parameter( property = "project" )
    protected MavenProject project;

    @Parameter( property = "outputPath", required = true, defaultValue = "target/")
    protected String outputPath = "target/";

    @Parameter( property = "sourceRoot" )
    protected String sourceRoot;

    public void setProject(MavenProject project) {
        this.project = project;
    }

    @SuppressWarnings("unchecked")
    protected String getTopLevelClassName( String sourceFile )
    {
        String className = sourceFile.substring( 0, sourceFile.length() - 5 ); // strip ".java"
        return className.replace( File.separatorChar, '.' );
    }

    /**
     * @return the project classloader
     * @throws org.apache.maven.artifact.DependencyResolutionRequiredException failed to resolve project dependencies
     * @throws java.net.MalformedURLException configuration issue ?
     */
    protected ClassLoader getProjectClassLoader()
            throws DependencyResolutionRequiredException, MalformedURLException {
        List<?> compile = project.getCompileClasspathElements();
        URL[] urls = new URL[compile.size()];
        int i = 0;
        for ( Object object : compile ) {
            if ( object instanceof Artifact) {
                urls[i] = ( (Artifact) object ).getFile().toURI().toURL();
            } else {
                urls[i] = new File( (String) object ).toURI().toURL();
            }
            i++;
        }
        return new URLClassLoader( urls, ClassLoader.getSystemClassLoader() );
    }

    protected String normalizeAnnotationValue( String value ) {
        if ( value == null ) {
            return null;
        }

        value = StringUtils.trim(value, " ");
        value = StringUtils.trim(value, "\"");
        value = StringUtils.trim(value, "/");
        value = StringUtils.trim(value, "\\");
        value = value.replaceAll("\" \\+ \"", "");
        return value;
    }

    /**
     * @param path file to add to the project compile directories
     */
    protected void addCompileSourceRoot( File path ) {
        project.addCompileSourceRoot( path.getAbsolutePath() );
    }

    protected JavaDocBuilder createJavaDocBuilder() throws MojoExecutionException {
        try {
            JavaDocBuilder builder = new JavaDocBuilder();
            builder.addSourceTree(new File(sourceRoot));
            builder.addSourceTree(new File("target/classes"));
            builder.getClassLibrary().addClassLoader( getProjectClassLoader() );
            for ( String sourceRoot : project.getCompileSourceRoots() ) {
                getLog().info("Sources root = " + sourceRoot );
                builder.addSourceTree( new File( sourceRoot ) );
            }
            return builder;
        } catch ( MalformedURLException e ) {
            throw new MojoExecutionException( "Failed to resolve project classpath", e );
        } catch ( DependencyResolutionRequiredException e ) {
            throw new MojoExecutionException( "Failed to resolve project classpath", e );
        }
    }

}
