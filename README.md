## Spring Data Generation Kit
-----
Version: 1.1.7-SNAPSHOT

1.1.7 changes

```
- Migration to QDox 2.0.0-M4
- Fix for a fields with a full-qualified type name (https://github.com/paul-hammant/qdox/issues/6)
- Source/target Java version updated to 1.8
- LICENSE updated
```

1.1.6_1 changes

```
- Fixed the problem with an enumeration as a type for the synthetic field
```

1.1.6 changes

```
- Ability to resolve DTO objects by an expression of parameter in @DtoExtend section (@see issue #13)

@DtoExtend({
    @Parameter( value = "company", type = CompanyDTO.class, expression = "participant.company" ),
})
class User ... {

    @ManyToOne
    @JoinColumn("idParticipant")
    @DtoInclude(AggregationType.ID)
    private CompanyParticipant participant;

}
```

1.1.5 changes

- @DtoDefaultValue - new annotation to provide the user with an ability to define the default value for the field of a simple type
 
 ```
 class Person ... 
 
    @DtoDefaultValue(value = "20", type = Integer.class)
    Integer age;
    
    @DtoDefaultValue(value="Michael", type = String.class )
    String firstName;
    
    @DtoDefaultValue(value="false", type = Boolean.class )
    Boolean atHome;
    
    @DtoDefaultValue(value="false", type = Boolean.class )
        Boolean atHome;
    
 ```

- Ability to make queries cacheable 

To achieve this you need to provide positive value for isCachable parameter of @NativeQuery or @ConventionalQuery annotation 
like in this example:

```
@NativeQuery(
    name = "doFindAllPersons",
    value = "select x from Person x",
    isCacheable = true
)
public class Person {

    ....

}
```

Its will results in next Spring Data code:
```
public interface IPersonDAO extends JpaRepository<Person, Long> {
 
    @NativeQuery("select x from Person x")
    @QueryHints( value = javax.persistence.QueryHint( name = "org.hibernate.cacheable", value = true ) )
    public List<Person> doFindAllPersons();
 
}
```

- Conversion service rewritten (to encapsulate particular type conversion) to avoid reflection usage
- Converters profiling implemented 

To turn on profiling on the converters side, you need to provide value for generator plugin @profilingEnabled property.
```
<plugin>
    <groupId>com.a5000.platform.opensource.generation-kit</groupId>
    <artifactId>generator-mojos</artifactId>
    <version>${project.version}</version>
    <configuration>
        <!-- .... -->
        <profilingEnabled>true</profilingEnabled>
        <!-- .... -->
    </configuration>
</plugin>
```

When a positive value has been provided, the plugin will generates converters which when invoked will print time they spent on particular 
transformation.

1.1.4 changes

- Some constants used by 'gen-jpa-converter' goal moved as a configuration parameters
```
<execution>
    <id>generate-converters</id>
    <phase>generate-sources</phase>
    <goals>
        <goal>gen-jpa-converter</goal>
    </goals>

    <configuration>
        <!-- Enable/disable annotation of converter methods by transactional marker -->
        <transactionAnnotationOnConverterMethods>true</transactionAnnotationOnConverterMethods>

        <!-- Annotation which will be used to annotate converters in the case when transactionAnnotationOnConverterMethods = true -->
        <transactionalAnnotation>org.springframework.transaction.annotation.Transactional</transactionalAnnotation>

        <!-- Interface which would be used by converters to represent basic JPA entity with ID -->
        <jpaEntityInterface>com.everydaygroup.edhanky.api.model.domain.api.IStoredBean</jpaEntityInterface>
    </configuration>
</execution>
```

1.1.3 changes

- Versions plugin added
- Added some unit tests to check the correctness of generated classes
- Fixing issue #11 (if no type information provided as a value of targetEntity for JPA relation (one-to-many, many-to-many, etc., then the list generic value would be used )

1.1.2 changes

- @Qualifier on abstract DAO to make possible to autowire them directly
- 'expression' value in @Parameter annotations (will be evaluated by generated DtoConversionService)

1.1.1 changes
- Converter logic issues fixed ( synthetic fields, handling cases with inaccessible or non-exists fields accessor, null-check added, etc. )
- DTO issues fixed (lists support improved,
    static & abstract methods generation added, DTO generic types handling improved )
- Some general problems solved: types comparing for a cases when package has
not been  detected by QDox, constants moved to abstract generator mojo

[new!] 1.1.0 chages
- New generators infrastructure
- FreeMarker generator replaced with Sun CodeModel
- Ability to specify methods with @DtoMethod

1.0.22 features
- Ability to specify custom interface which would be implemented by generated DTO classes

1.0.21 features

- Conversion service template changed to improve synthetic fields handling

1.0.18 features

- Support for conversing lists from JPA to DTO entities

1.0.16 features

- import issue fix

1.0.14 features

- Support for @Transactional on generated conventional and native queries

1.0.12 features

- transient fields support

## Main Features
- Basic features of simple POJO generation to transfer between separate JVM processes or
to be used in a sophisticated environments with some API restrictions such like Android or GWT
- Inheritance relations coverage
- Wide abilities to affect on a generation result
- - Fields excluding / including
- - Extending entity DTO result by a fields from another entities
- - Ability to create DTO groups
- Automatic Spring Data repositories generation API (conventional-like and JPQL-based)
- Automatic conversation service from JPA to DTO

## Description

Plugin will help you to forget about manual edition & creation of
the DTOs and the repositories for your domain objects.

Example maven configuration:
```
            <plugin>
                <groupId>com.a5000.platform.opensource.generation-kit</groupId>
                <artifactId>generator-mojos</artifactId>
                <configuration>
                    <sourceRoot>${project.build.sourceDirectory}</sourceRoot>
                    <entityPattern>**/model/**/*.java</entityPattern>
                    <basePackage>com.a5000.platform.api.model</basePackage>
                    <dtoPackage>com.a5000.platform.api.model.domain</dtoPackage>
                    <convertersPackage>com.a5000.platform.api.services</convertersPackage>
                    <daoPackage>com.a5000.platform.dao</daoPackage>

                    <dtoAnnotationClasses>
                        <dtoAnnotationClass>com.gwtent.reflection.client.Reflectable</dtoAnnotationClass>
                    </dtoAnnotationClasses>

                    <dtoInterfaceClasses>
                        <dtoInterfaceClass>java.io.Serializable</dtoInterfaceClass>
                        <dtoInterfaceClass>com.a5000.platform.api.model.domain.api.Identifiable</dtoInterfaceClass>
                    </dtoInterfaceClasses>

                    <skipStaticFields>true</skipStaticFields>

                    <attachPostfixes>true</attachPostfixes>
                    <attachPrefixes>true</attachPrefixes>
                    <attachSuffixes>false</attachSuffixes>

                    <outputPath>${project.basedir}/src/main/generated</outputPath>
                </configuration>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>gen-dao</goal>
                            <goal>gen-dto</goal>
                            <goal>gen-jpa-converter</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

Also, to cover situation which are often in a real life, when domain entities is not holding under a
single root-package, in 1.0.2 version introduced ability to define several base packages to process and scan:
```
    <configuration>
        ....
        <basePackagePaths>
                <basePackage>com.a5000.platform.api.model</basePackage>
                <dtoPackage>com.a5000.platform.api.model.domain</dtoPackage>
                <convertersPackage>com.a5000.platform.api.services</convertersPackage>
                <daoPackage>com.a5000.platform.dao</daoPackage>
        </basePackagePaths>
        ....
    </configuration>
```

If you not need in one or more plugin generation aspect(s), this aspect(s) could be skipped by providing
appropriate configuration option:
```
    <configuration>
        ...
        <skipDaoGeneration>true</skipDaoGeneration>
        <skipDtoGeneration>false</skipDtoGeneration>
        <skipServicesGeneration>true</skipServicesGeneration>
        <skipStaticFields>true</skipStaticFields>
        ...
    </configuration>
```

==== Entities lookup & matching

Base regular expression to lookup entities in accessible classpath environment is ```**\/entities\/**\/*.java```.
It can be changed through <entityPattern/> configuration option in the next way:
```
    <configuration>
        ...
        <entityPattern>**/model/**/*.java</entityPattern>
        ...
    </configuration>
```

If you select directory which different from src/main/java, you also needs to add build-helper plugin, which
add this directory as a source root:
```
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>add-source</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>src/main/generated</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Also you must place in `<dependencies/>` link to annotation classes which must
be used to markdown JPA entities and its fields:
```
<dependency>
    <groupId>com.a5000.platform.opensource.generation-kit</groupId>
    <artifactId>generator-annotations</artifactId>
    <scope>provided</scope>
</dependency>
```

Here is some basic example of generator annotations usage:
```
@Entity
@ConventionalQueries({
    @ConventionalQuery( name = "findByAuthor", isPageable = true, parameters = {
        @com.redshape.odd.data.annotations.Parameter( value = "author", type = com.redshape.odd.data.entities.User.class )
    }),
    @ConventionalQuery( name = "findByAuthorId", isPageable = true, parameters = {
        @Parameter( value = "authorId", type = Long.class )
    }),
    @ConventionalQuery( name = "findByStatus", isPageable = true, parameters = {
        @Parameter( value = "status", type = com.redshape.odd.data.entities.PropositionStatus.class )
    }),
    @ConventionalQuery( name = "findByAuthorIdAndId", isCollection = false, parameters = {
        @Parameter( value = "authorId", type = Long.class ),
        @Parameter( value = "id", type = Long.class )
    }),
    @ConventionalQuery( name = "findByTextLike", isPageable = true, parameters = {
        @Parameter( value = "queryText", type = String.class )
    }),
    @ConventionalQuery( name = "findBySpecialisationIdInAndTextLike", isPageable = true, parameters = {
        @Parameter( value = "specialisations", isArray = true, type = Long.class ),
        @Parameter( value = "queryText", type = String.class )
    }),
    @ConventionalQuery( name = "findBySpecialisationIdIn", isPageable = true, parameters = {
        @Parameter( value = "specialisations", isArray = true, type = Long.class )
    }),
    @ConventionalQuery( name = "findByTypeInAndSpecialisationIdInAndTextLike", isPageable = true, parameters = {
        @Parameter( value = "type", isArray = true, type = com.redshape.odd.data.entities.PropositionType.class ),
        @Parameter( value = "specialisations", isArray = true, type = Long.class ),
        @Parameter( value = "queryText", type = String.class )
    }),
    @ConventionalQuery( name = "findByTypeInAndTextLike", isPageable = true, parameters = {
        @Parameter( value = "type", isArray = true, type = com.redshape.odd.data.entities.PropositionType.class ),
        @Parameter( value = "queryText", type = String.class )
    }),
    @ConventionalQuery( name = "findByTypeIn", isPageable = true, parameters = {
        @Parameter( value = "type", isArray = true, type = com.redshape.odd.data.entities.PropositionType.class )
    })
})
@DtoExtend({
    @Parameter( value = "propositionsCount", type = int.class ),
    /**
     * PropositionDTO gain new field - {@code String relatedAuthorName} with value getAuthor().getName()
     **/
    @Parameter( value = "relatedAuthorName", expression = "author.name", type = String.class )
})
public class Proposition implements Serializable {

    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;

    @ManyToOne
    @JoinColumn( name = "author_id" )
    private User author;

    @ManyToOne
    @JoinColumn( name = "specialisation_id" )
    @DtoInclude(AggregationType.DTO)
    private Specialisation specialisation;

    // Accessors and everything else goes here..

    /**
     * All methods marked as a @DtoMethod will survive in DTO entity
     **/
    @DtoMethod
    public void calculateId() {
        return (this.id + this.author.getName()).hashCode();
    }
}
```

=== Authors

+ Cyril A. Karpenko <self@nikelin.ru>

=== License

Copyright 2013-2014, Cyril A. Karpenko

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
