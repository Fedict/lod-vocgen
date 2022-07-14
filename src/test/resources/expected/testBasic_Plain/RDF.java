package org.w3.vocab;

/**
 * Constants for the The RDF vocabulary.
 *
 * @see <a href="https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/">The RDF vocabulary</a>
 */
public class RDF {

    /**
     * The RDF namespace: http://www.w3.org/1999/02/22-rdf-syntax-ns#
     */
    public static final String NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * Returns the URI for this schema
     * @return URI
     */
    public static String getURI() {
        return NS;
    }
    
    // Classes
    public static final String Statement = NS + "Statement";
    public static final String Alt = NS + "Alt";
    public static final String Bag = NS + "Bag";
    public static final String List = NS + "List";
    public static final String Property = NS + "Property";
    public static final String CompoundLiteral = NS + "CompoundLiteral";
    public static final String Seq = NS + "Seq";

    // Properties
    public static final String predicate = NS + "predicate";
    public static final String rest = NS + "rest";
    public static final String subject = NS + "subject";
    public static final String language = NS + "language";
    public static final String type = NS + "type";
    public static final String value = NS + "value";
    public static final String first = NS + "first";
    public static final String object = NS + "object";
    public static final String direction = NS + "direction";

    // Individuals
    public static final String nil = NS + "nil";
    public static final String rest = NS + "rest";
    public static final String predicate = NS + "predicate";
    public static final String subject = NS + "subject";
    public static final String language = NS + "language";
    public static final String type = NS + "type";
    public static final String value = NS + "value";
    public static final String first = NS + "first";
    public static final String object = NS + "object";
    public static final String direction = NS + "direction";
}
