# lod-vocgen

Simple vocabulary class generator for [Apache Jena](https://jena.apache.org/) and [Eclipse RDF4J](http://rdf4j.org/).

There are much more linked data vocabularies / ontologies than available in Jena or RDF4J,
so this tool can be used to generate Java helper classes using an existing OWL file (in Turtle).

The java classes are generated using the [Apache Freemarker](http://freemarker.org/) template engine.

## Differences in coding style

  * RDF4J uses all-uppercase in constants (in line with Java best practices), 
Jena uses a mixture of lower/uppercase (following the case in the vocabularies).
  * RDF4J uses tabs, Jena uses white spaces for indentation.
  * Most RDF4J classes include the name of the author of the java file, while Jena discourages it. 

## Example

```
java -jar target/vocgen-[version]-with-dependencies.jar --file <local_vocab_owl.ttl> --doc <vocab_human_doc_url>
	--ns <namespace_uri> --prefix <preferred_prefix>
	--short <short_vocabulary_name> --long <long_vocabulary_name>
	--author <name_java_author>
```

## Usage in maven exec plugin

To include this generator in a maven toolchain, add the dependency to your pom file and run it using the exec-maven-plugin along these lines:
```
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.0.0</version> <!-- check for updates! -->
    <executions>
        <execution>
            <goals>
                <goal>java</goal>
            </goals>
            <phase>generate-sources</phase>
        </execution>
    </executions>
    <configuration>
        <includeProjectDependencies>false</includeProjectDependencies> <!-- unless your ontology/copyright file is in a project dependency -->
        <includePluginDependencies>true</includePluginDependencies>    
        <mainClass>be.belgif.vocgen.Main</mainClass>
        <arguments>
            <argument>--searchClasspath</argument> <!-- if your ontology/copyright are on the classpath -->  
            <argument>--file</argument><argument>src/main/resources/your-ontology.ttl</argument>
            <argument>--long</argument><argument>The long name of you ontology</argument>
            <argument>--short</argument><argument>YONT</argument> <!-- this will also be the name of your java class -->
            <argument>--ns</argument><argument>https://example.com/your-ontology</argument>
            <argument>--prefix</argument><argument>yont</argument>
            <argument>--doc</argument><argument>https://www.example.com/your-ontology-documentation</argument>
            <argument>--author</argument><argument>the esteemed author</argument>
            <argument>--package</argument><argument>com.example.yont</argument>
            <argument>--output-dir</argument><argument>${project.build.directory}/generated-sources/com/example/yont</argument>
            <argument>--template</argument><argument>rdf4j</argument>
        </arguments>
    </configuration>
     <dependencies>
        <dependency>
            <groupId>be.belgif</groupId>
            <artifactId>VocGen</artifactId>
            <version>1.0.3</version>
            <type>jar</type>
        </dependency>
    </dependencies>
</plugin>
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <version>3.2.0</version>
    <executions>
        <execution>
            <phase>process-sources</phase>
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>${project.build.directory}/generated-sources/</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>
```