package org.w3.vocab;

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
    /** <code>Alt</code> **/
    public static final Resource Alt = m.createResource(NS + "Alt");
    /** <code>Bag</code> **/
    public static final Resource Bag = m.createResource(NS + "Bag");
    /** <code>CompoundLiteral</code> **/
    public static final Resource CompoundLiteral = m.createResource(NS + "CompoundLiteral");
    /** <code>List</code> **/
    public static final Resource List = m.createResource(NS + "List");
    /** <code>Property</code> **/
    public static final Resource Property = m.createResource(NS + "Property");
    /** <code>Seq</code> **/
    public static final Resource Seq = m.createResource(NS + "Seq");
    /** <code>Statement</code> **/
    public static final Resource Statement = m.createResource(NS + "Statement");

    // Properties
    /** <code>direction</code> **/
    public static final Property direction = m.createProperty(NS + "direction");
    /** <code>first</code> **/
    public static final Property first = m.createProperty(NS + "first");
    /** <code>language</code> **/
    public static final Property language = m.createProperty(NS + "language");
    /** <code>object</code> **/
    public static final Property object = m.createProperty(NS + "object");
    /** <code>predicate</code> **/
    public static final Property predicate = m.createProperty(NS + "predicate");
    /** <code>rest</code> **/
    public static final Property rest = m.createProperty(NS + "rest");
    /** <code>subject</code> **/
    public static final Property subject = m.createProperty(NS + "subject");
    /** <code>type</code> **/
    public static final Property type = m.createProperty(NS + "type");
    /** <code>value</code> **/
    public static final Property value = m.createProperty(NS + "value");

    // Individuals
        /** <code>nil</code> **/
    public static final Individual nil = m.createProperty(NS + "nil");
}
