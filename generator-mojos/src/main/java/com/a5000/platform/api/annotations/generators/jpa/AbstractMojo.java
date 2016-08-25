package com.a5000.platform.api.annotations.generators.jpa;

import com.a5000.platform.api.annotations.generators.jpa.utils.StringUtils;
import com.thoughtworks.qdox.JavaProjectBuilder;
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

    protected JavaProjectBuilder createJavaDocBuilder() throws MojoExecutionException {
        try {
            JavaProjectBuilder builder =new JavaProjectBuilder();
            builder.addSourceTree(new File(sourceRoot));
            builder.addSourceTree(new File("target/classes"));
            builder.addClassLoader( getProjectClassLoader() );
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
