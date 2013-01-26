package com.redshape.generators.jpa;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import com.redshape.generators.jpa.utils.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @implementation com.redshape.generators.jpa.GeneratorMojo
 */
public abstract class AbstractMojo extends org.apache.maven.plugin.AbstractMojo {

    /**
     * @parameter expression="${plugin.artifacts}" required="true"
     */
    private Collection<Artifact> pluginArtifacts;

    /**
     * @component
     */
    protected ArtifactResolver resolver;

    /**
     * @component
     */
    protected ArtifactFactory artifactFactory;


    /**
     * @required
     * @readonly
     * @component
     */
    protected ClasspathBuilder classpathBuilder;

    // --- Some MavenSession related structures --------------------------------

    /**
     * @parameter expression="${localRepository}" required="true"
     */
    protected ArtifactRepository localRepository;

    /**
     * @parameter expression="${project.remoteArtifactRepositories}" required="true"
     */
    protected List<ArtifactRepository> remoteRepositories;

    /**
     * @component
     */
    protected ArtifactMetadataSource artifactMetadataSource;

    /**
     * The maven project descriptor
     *
     * @parameter expression="${project}" required="true"
     */
    private MavenProject project;

    // --- Plugin parameters ---------------------------------------------------

    /**
     * Folder where generated-source for data transfer objects
     * will be created (automatically added to compile classpath).
     *
     * @parameter default-value="${project.build.directory}/generated-sources" required="true"
     */
    private File dtoGenerateDirectory;

    /**
     * Folder where generated-source for data access objects
     * will be created (automatically added to compile classpath).
     *
     * @parameter default-value="${project.build.directory}/generated-sources" required="true"
     */
    private File daoGenerateDirectory;

    protected String prepareTypeName( boolean isArray, String name ) {
        if ( name.endsWith(".class") ) {
            name = name.substring( 0, name.length() - ".class".length() );
        }

        if ( isArray ) {
            name += "[]";
        }

        return name;
    }

    protected String normalizeAnnotationValue( String value ) {
        value = StringUtils.trim(value, "\"");
        value = StringUtils.trim( value, "/");
        value = StringUtils.trim( value, "\\");
        return value;
    }

    /**
     * @param path file to add to the project compile directories
     */
    protected void addCompileSourceRoot( File path )
    {
        getProject().addCompileSourceRoot( path.getAbsolutePath() );
    }

    public Collection<Artifact> getPluginArtifacts() {
        return pluginArtifacts;
    }

    public void setPluginArtifacts(Collection<Artifact> pluginArtifacts) {
        this.pluginArtifacts = pluginArtifacts;
    }

    public ArtifactResolver getResolver() {
        return resolver;
    }

    public void setResolver(ArtifactResolver resolver) {
        this.resolver = resolver;
    }

    public ArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }

    public void setArtifactFactory(ArtifactFactory artifactFactory) {
        this.artifactFactory = artifactFactory;
    }

    public ClasspathBuilder getClasspathBuilder() {
        return classpathBuilder;
    }

    public void setClasspathBuilder(ClasspathBuilder classpathBuilder) {
        this.classpathBuilder = classpathBuilder;
    }

    public ArtifactRepository getLocalRepository() {
        return localRepository;
    }

    public void setLocalRepository(ArtifactRepository localRepository) {
        this.localRepository = localRepository;
    }

    public List<ArtifactRepository> getRemoteRepositories() {
        return remoteRepositories;
    }

    public void setRemoteRepositories(List<ArtifactRepository> remoteRepositories) {
        this.remoteRepositories = remoteRepositories;
    }

    public ArtifactMetadataSource getArtifactMetadataSource() {
        return artifactMetadataSource;
    }

    public void setArtifactMetadataSource(ArtifactMetadataSource artifactMetadataSource) {
        this.artifactMetadataSource = artifactMetadataSource;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public File getDtoGenerateDirectory() {
        return dtoGenerateDirectory;
    }

    public void setDtoGenerateDirectory(File generateDirectory) {
        this.dtoGenerateDirectory = generateDirectory;
    }

    public File getDaoGenerateDirectory() {
        return daoGenerateDirectory;
    }

    public void setDaoGenerateDirectory(File generateDirectory) {
        this.daoGenerateDirectory = generateDirectory;
    }

    protected Artifact getArtifact( String groupId, String artifactId, String classifier )
    {
        for ( Artifact artifact : pluginArtifacts )
        {
            if ( groupId.equals( artifact.getGroupId() ) && artifactId.equals( artifact.getArtifactId() ) )
            {
                if ( classifier != null && classifier.equals( artifact.getClassifier() ) )
                {
                    return artifact;
                }
                if ( classifier == null && artifact.getClassifier() == null )
                {
                    return artifact;
                }
            }
        }
        getLog().error( "Failed to retrieve " + groupId + ":" + artifactId + ":" + classifier );
        return null;
    }

    protected Artifact resolve( String groupId, String artifactId, String version, String type, String classifier )
            throws MojoExecutionException
    {
        // return project.getArtifactMap().get( groupId + ":" + artifactId );

        Artifact artifact = artifactFactory.createArtifactWithClassifier( groupId, artifactId, version, type, classifier );
        try
        {
            resolver.resolve(artifact, remoteRepositories, localRepository);
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "artifact not found - " + e.getMessage(), e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( "artifact resolver problem - " + e.getMessage(), e );
        }
        return artifact;
    }

    public Set<Artifact> getProjectRuntimeArtifacts()
    {
        Set<Artifact> artifacts = new HashSet<Artifact>();
        for (Artifact projectArtifact : (Collection<Artifact>) project.getArtifacts() )
        {
            String scope = projectArtifact.getScope();
            if ( Artifact.SCOPE_RUNTIME.equals( scope )
                    || Artifact.SCOPE_COMPILE.equals( scope ) )
            {
                artifacts.add( projectArtifact );
            }

        }
        return artifacts;
    }

}
