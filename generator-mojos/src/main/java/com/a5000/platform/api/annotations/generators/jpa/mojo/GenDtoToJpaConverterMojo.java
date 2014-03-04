package com.a5000.platform.api.annotations.generators.jpa.mojo;

import com.a5000.platform.api.annotations.generators.jpa.AbstractGeneratorMojo;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.thoughtworks.qdox.model.JavaClass;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Created by cyril on 8/28/13.
 */
@Mojo( name = "gen-dto-converter", threadSafe = true )
public class GenDtoToJpaConverterMojo extends AbstractGeneratorMojo {

    private static final String CONVERTER_CLASS_NAME = "JpaHydrationService";

    private JDefinedClass converterService;

    public GenDtoToJpaConverterMojo() {
        super("DTO to JPA conversion services generator", "", "", "");
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
        init();

        generateTemplateConverterMethod();
        generateTemplateListConverterMethod();

        super.execute();
    }

    private void generateTemplateListConverterMethod() {

    }

    /**
     *
     * public User hydrateRecord( UserDTO record ) {
     *     User result;
     *     if ( record.getId() == null ) {
     *         result = new User();
     *     } else {
     *         result = usersDAO.findOne( record.getId() );
     *     }
     *
     *     if ( result
     * }
     *
     * public <T extends IStoredBean, V> T convertToDto( V dto ) {
     *
     * }
     *
     */
    protected void generateTemplateConverterMethod() {
    }

    protected void init() throws MojoExecutionException {
        try {
            this.converterService = codeModel._package(convertersPackage)
               ._class(CONVERTER_CLASS_NAME);
        } catch (JClassAlreadyExistsException e) {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private void generateConverter(JavaClass entityClazz) {

    }

    @Override
    protected boolean isSupported(JavaClass entityClass) {
        return isJpaEntity(entityClass);
    }
}
