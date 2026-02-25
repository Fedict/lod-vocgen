package org.w3.vocab;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;


/**
 * Constants for the The RDF vocabulary.
 *
 * @see <a href="https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/">The RDF vocabulary</a>
 * 
 *	@author The Author 
 */
public class RDF {
	/**
	 * The RDF namespace: http://www.w3.org/1999/02/22-rdf-syntax-ns#
	 */
	public static final String NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	/**
	 * Recommended prefix for the namespace: "rdf"
	 */
	public static final String PREFIX = "rdf";

	/**
	 * An immutable {@link Namespace} constant that represents the namespace.
	 */
	public static final Namespace NS = new SimpleNamespace(PREFIX, NAMESPACE);

	// Classes
    /** <code>rdf:Alt</code> **/
	public static final IRI Alt = create("Alt");

    /** <code>rdf:Bag</code> **/
	public static final IRI Bag = create("Bag");

    /** <code>rdf:CompoundLiteral</code> **/
	public static final IRI CompoundLiteral = create("CompoundLiteral");

    /** <code>rdf:List</code> **/
	public static final IRI List = create("List");

    /** <code>rdf:Property</code> **/
	public static final IRI Property = create("Property");

    /** <code>rdf:Seq</code> **/
	public static final IRI Seq = create("Seq");

    /** <code>rdf:Statement</code> **/
	public static final IRI Statement = create("Statement");


	// Properties
    /** <code>rdf:direction</code> **/
	public static final IRI direction = create("direction");

    /** <code>rdf:first</code> **/
	public static final IRI first = create("first");

    /** <code>rdf:language</code> **/
	public static final IRI language = create("language");

    /** <code>rdf:object</code> **/
	public static final IRI object = create("object");

    /** <code>rdf:predicate</code> **/
	public static final IRI predicate = create("predicate");

    /** <code>rdf:rest</code> **/
	public static final IRI rest = create("rest");

    /** <code>rdf:subject</code> **/
	public static final IRI subject = create("subject");

    /** <code>rdf:type</code> **/
	public static final IRI type = create("type");

    /** <code>rdf:value</code> **/
	public static final IRI value = create("value");


	// Individuals
    /** <code>rdf:nil</code> **/
	public static final IRI nil = create("nil");


	private static IRI create(String localName) {
		return SimpleValueFactory.getInstance().createIRI(RDF.NAMESPACE, localName);
	}
}

