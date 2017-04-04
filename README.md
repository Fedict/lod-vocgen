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
java -jar vocgen.jar --file <local_vocab_owl.ttl> --doc <vocab_human_doc_url>
	--ns <namespace_uri> --prefix <preferred_prefix>
	--short <short_vocabulary_name> --long <long_vocabulary_name>
	--author <name_java_author>
```

