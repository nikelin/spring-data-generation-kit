package com.a5000.platform.api.annotations.generators.jpa;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.File;
import java.util.*;

import static org.apache.maven.artifact.Artifact.*;

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
 * Util to consolidate classpath manipulation stuff in one place.
 *
 * @version $Id$
 * @plexus.component role="com.redshape.generators.jpa.ClasspathBuilder"
 */
public class ClasspathBuilder
    extends AbstractLogEnabled
{

    @SuppressWarnings( "unchecked" )
    public Collection<File> buildClasspathList( final MavenProject project, final String scope,
                                                Set<Artifact> artifacts )
        throws ClasspathBuilderException
    {
        getLogger().debug( "establishing classpath list (scope = " + scope + ")" );

        Set<File> items = new LinkedHashSet<File>();

        items.add( new File( project.getBuild().getOutputDirectory() ) );
        addSources( items, project.getCompileSourceRoots() );
        addResources( items, project.getResources() );

        if ( scope.equals( SCOPE_TEST ) )
        {
            addSources( items, project.getTestCompileSourceRoots() );
            addResources( items, project.getTestResources() );
            items.add( new File( project.getBuild().getTestOutputDirectory() ) );

            for ( Artifact artifact : artifacts )
            {
                items.add( artifact.getFile() );
            }
        }
        else if ( scope.equals( SCOPE_COMPILE ) )
        {
            getLogger().debug( "candidate artifacts : " + artifacts.size() );
            for ( Artifact artifact : artifacts )
            {
                String artifactScope = artifact.getScope();
                if ( SCOPE_COMPILE.equals( artifactScope ) || SCOPE_PROVIDED.equals( artifactScope )
                    || SCOPE_SYSTEM.equals( artifactScope ) )
                {
                    items.add( artifact.getFile() );
                }
            }
        }
        else if ( scope.equals( SCOPE_RUNTIME ) )
        {
            for ( Artifact artifact : artifacts )
            {
                getLogger().debug( "candidate artifact : " + artifact );
                if ( !artifact.getScope().equals( SCOPE_TEST ) && artifact.getArtifactHandler().isAddedToClasspath() )
                {
                    items.add( artifact.getFile() );
                }
            }
        }
        else
        {
            throw new ClasspathBuilderException( "unsupported scope " + scope );
        }
        return items;
    }

    public void addSourcesWithActiveProjects( final MavenProject project, final Collection<File> items,
                                              final String scope )
    {
        final List<Artifact> scopeArtifacts = getScopeArtifacts( project, scope );

        addSources( items, getSourceRoots( project, scope ) );

        for ( Artifact artifact : scopeArtifacts )
        {
            String projectReferenceId =
                getProjectReferenceId( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion() );
            MavenProject refProject = (MavenProject) project.getProjectReferences().get( projectReferenceId );
            if ( refProject != null )
            {
                addSources( items, getSourceRoots( refProject, scope ) );
            }
        }
    }

    public void addResourcesWithActiveProjects( final MavenProject project, final Collection<File> items,
                                                final String scope )
    {
        final List<Artifact> scopeArtifacts = getScopeArtifacts( project, scope );

        addResources( items, getResources( project, scope ) );

        for ( Artifact artifact : scopeArtifacts )
        {
            String projectReferenceId =
                getProjectReferenceId( artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion() );
            MavenProject refProject = (MavenProject) project.getProjectReferences().get( projectReferenceId );
            if ( refProject != null )
            {
                addResources( items, getResources( refProject, scope ) );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<Artifact> getScopeArtifacts( final MavenProject project, final String scope )
    {
        if ( SCOPE_COMPILE.equals( scope ) )
        {
            return project.getCompileArtifacts();
        }
        if ( SCOPE_RUNTIME.equals( scope ) )
        {
            return project.getRuntimeArtifacts();
        }
        else if ( SCOPE_TEST.equals( scope ) )
        {
            return project.getTestArtifacts();
        }
        else
        {
            throw new RuntimeException( "Not allowed scope " + scope );
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getSourceRoots( final MavenProject project, final String scope )
    {
        if ( SCOPE_COMPILE.equals( scope ) || SCOPE_RUNTIME.equals( scope ) )
        {
            return project.getCompileSourceRoots();
        }
        else if ( SCOPE_TEST.equals( scope ) )
        {
            List<String> sourceRoots = new ArrayList<String>();
            sourceRoots.addAll( project.getTestCompileSourceRoots() );
            sourceRoots.addAll( project.getCompileSourceRoots() );
            return sourceRoots;
        }
        else
        {
            throw new RuntimeException( "Not allowed scope " + scope );
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<Resource> getResources( final MavenProject project, final String scope )
    {
        if ( SCOPE_COMPILE.equals( scope ) || SCOPE_RUNTIME.equals( scope ) )
        {
            return project.getResources();
        }
        else if ( SCOPE_TEST.equals( scope ) )
        {
            List<Resource> resources = new ArrayList<Resource>();
            resources.addAll( project.getTestResources() );
            resources.addAll( project.getResources() );
            return resources;
        }
        else
        {
            throw new RuntimeException( "Not allowed scope " + scope );
        }
    }

    private void addSources( final Collection<File> items, final Collection<String> sourceRoots )
    {
        for ( String path : sourceRoots )
        {
            items.add( new File( path ) );
        }
    }

    private void addResources( final Collection<File> items, final Collection<Resource> resources )
    {
        for ( Resource resource : resources )
        {
            items.add( new File( resource.getDirectory() ) );
        }
    }

    private String getProjectReferenceId( final String groupId, final String artifactId, final String version )
    {
        return groupId + ":" + artifactId + ":" + version;
    }
}