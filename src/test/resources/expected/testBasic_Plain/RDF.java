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
    /** <code>Alt</code> **/
    public static final String Alt = NS + "Alt";
    /** <code>Bag</code> **/
    public static final String Bag = NS + "Bag";
    /** <code>CompoundLiteral</code> **/
    public static final String CompoundLiteral = NS + "CompoundLiteral";
    /** <code>List</code> **/
    public static final String List = NS + "List";
    /** <code>Property</code> **/
    public static final String Property = NS + "Property";
    /** <code>Seq</code> **/
    public static final String Seq = NS + "Seq";
    /** <code>Statement</code> **/
    public static final String Statement = NS + "Statement";

    // Properties
    /** <code>direction</code> **/
    public static final String direction = NS + "direction";
    /** <code>first</code> **/
    public static final String first = NS + "first";
    /** <code>language</code> **/
    public static final String language = NS + "language";
    /** <code>object</code> **/
    public static final String object = NS + "object";
    /** <code>predicate</code> **/
    public static final String predicate = NS + "predicate";
    /** <code>rest</code> **/
    public static final String rest = NS + "rest";
    /** <code>subject</code> **/
    public static final String subject = NS + "subject";
    /** <code>type</code> **/
    public static final String type = NS + "type";
    /** <code>value</code> **/
    public static final String value = NS + "value";

    // Individuals
    /** <code>nil</code> **/
    public static final String nil = NS + "nil";
}
