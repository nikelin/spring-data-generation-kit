package com.a5000.platform.api.annotations.generators.jpa.mojo;

import com.a5000.platform.api.annotations.generators.jpa.AbstractGeneratorMojo;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.thoughtworks.qdox.model.JavaClass;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

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
