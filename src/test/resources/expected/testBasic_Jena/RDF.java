package org.apache.jena.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Individual;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * Constants for the The RDF vocabulary.
 *
 * @see <a href="https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/">The RDF vocabulary</a>
 */
public class RDF {
    private static final Model m = ModelFactory.createDefaultModel();

    /**
     * The RDF namespace: http://www.w3.org/1999/02/22-rdf-syntax-ns#
     */
    public static final String NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final Resource NAMESPACE = m.createResource(NS);

    /**
     * Returns the URI for this schema
     * @return URI
     */
    public static String getURI() {
        return NS;
    }
    
    // Classes
    public static final Resource Statement = m.createResource(NS + "Statement");
    public static final Resource Alt = m.createResource(NS + "Alt");
    public static final Resource Bag = m.createResource(NS + "Bag");
    public static final Resource List = m.createResource(NS + "List");
    public static final Resource Property = m.createResource(NS + "Property");
    public static final Resource CompoundLiteral = m.createResource(NS + "CompoundLiteral");
    public static final Resource Seq = m.createResource(NS + "Seq");

    // Properties
    public static final Property predicate = m.createProperty(NS + "predicate");
    public static final Property rest = m.createProperty(NS + "rest");
    public static final Property subject = m.createProperty(NS + "subject");
    public static final Property language = m.createProperty(NS + "language");
    public static final Property type = m.createProperty(NS + "type");
    public static final Property value = m.createProperty(NS + "value");
    public static final Property first = m.createProperty(NS + "first");
    public static final Property object = m.createProperty(NS + "object");
    public static final Property direction = m.createProperty(NS + "direction");

    // Individuals
    public static final Individual nil = m.createProperty(NS + "nil");
    public static final Individual rest = m.createProperty(NS + "rest");
    public static final Individual predicate = m.createProperty(NS + "predicate");
    public static final Individual subject = m.createProperty(NS + "subject");
    public static final Individual language = m.createProperty(NS + "language");
    public static final Individual type = m.createProperty(NS + "type");
    public static final Individual value = m.createProperty(NS + "value");
    public static final Individual first = m.createProperty(NS + "first");
    public static final Individual object = m.createProperty(NS + "object");
    public static final Individual direction = m.createProperty(NS + "direction");
}
