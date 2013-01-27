## Spring Data Generation Kit
-----

Can be used as a generation-time plugin for your Maven project.

Plugin will help you to forget about manual edition & creation of
the DTOs and the repositories for your domain objects.

Example maven configuration:
```
<plugin>
    <groupId>com.redshape.utils.generation-kit</groupId>
    <artifactId>spring-data-generator</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <dtoGenerateDirectory>src/main/generated</dtoGenerateDirectory>
        <daoGenerateDirectory>src/main/generated</daoGenerateDirectory>
        <dtoPackagePath>org.example.data.entities.dto</dtoPackagePath>
        <daoPackagePath>org.example.data.entities.dao</daoPackagePath>
        <conversationServicePackagePath>org.example.example.services</conversationServicePackagePath>
        <basePackagePath>org.example.data.entities</basePackagePath>
    </configuration>
</plugin>
```

Public plugins repository:
```
<pluginRepositories>
    <pluginRepository>
        <id>redshape.central</id>
        <url>http://78.47.14.237:8081/artifactory/plugins-public-snapshots</url>
        <name>Redshape Central Repository</name>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </pluginRepository>
</pluginRepositories>
```

If you select directory which different from src/main/java, you also needs to add build-helper plugin, which
add this directory as a source root:
```
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <version>1.7</version>
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
    <groupId>com.redshape.utils.generation-kit</groupId>
    <artifactId>generator-annotations</artifactId>
    <version>1.0-SNAPSHOT</version>
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
    @Parameter( value = "propositionsCount", type = int.class )
})
public class Proposition implements Serializable {

    @Id @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;

    @ManyToOne
    @JoinColumn( name = "author_id" )
    @DtoInclude(AggregationType.DTO)
    private User author;

    @ManyToOne
    @JoinColumn( name = "specialisation_id" )
    @DtoInclude(AggregationType.DTO)
    private Specialisation specialisation;

    // Accessors and everything else goes here..
}
```

=== Authors

+ Cyril A. Karpenko <self@nikelin.ru>

=== License

Copyright 2013, Cyril A. Karpenko

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.