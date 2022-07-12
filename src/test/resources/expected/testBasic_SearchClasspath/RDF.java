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
	/** <tt>rdf:Statement</tt> */
	public static final IRI Statement = create("Statement");

	/** <tt>rdf:Alt</tt> */
	public static final IRI Alt = create("Alt");

	/** <tt>rdf:Bag</tt> */
	public static final IRI Bag = create("Bag");

	/** <tt>rdf:List</tt> */
	public static final IRI List = create("List");

	/** <tt>rdf:Property</tt> */
	public static final IRI Property = create("Property");

	/** <tt>rdf:CompoundLiteral</tt> */
	public static final IRI CompoundLiteral = create("CompoundLiteral");

	/** <tt>rdf:Seq</tt> */
	public static final IRI Seq = create("Seq");


	// Properties
	/** <tt>rdf:predicate</tt> */
	public static final IRI predicate = create("predicate");

	/** <tt>rdf:rest</tt> */
	public static final IRI rest = create("rest");

	/** <tt>rdf:subject</tt> */
	public static final IRI subject = create("subject");

	/** <tt>rdf:language</tt> */
	public static final IRI language = create("language");

	/** <tt>rdf:type</tt> */
	public static final IRI type = create("type");

	/** <tt>rdf:value</tt> */
	public static final IRI value = create("value");

	/** <tt>rdf:first</tt> */
	public static final IRI first = create("first");

	/** <tt>rdf:object</tt> */
	public static final IRI object = create("object");

	/** <tt>rdf:direction</tt> */
	public static final IRI direction = create("direction");


	// Individuals
	/** <tt>rdf:nil</tt> */
	public static final IRI nil = create("nil");

	/** <tt>rdf:rest</tt> */
	public static final IRI rest = create("rest");

	/** <tt>rdf:predicate</tt> */
	public static final IRI predicate = create("predicate");

	/** <tt>rdf:subject</tt> */
	public static final IRI subject = create("subject");

	/** <tt>rdf:language</tt> */
	public static final IRI language = create("language");

	/** <tt>rdf:type</tt> */
	public static final IRI type = create("type");

	/** <tt>rdf:value</tt> */
	public static final IRI value = create("value");

	/** <tt>rdf:first</tt> */
	public static final IRI first = create("first");

	/** <tt>rdf:object</tt> */
	public static final IRI object = create("object");

	/** <tt>rdf:direction</tt> */
	public static final IRI direction = create("direction");


	private static IRI create(String localName) {
		return SimpleValueFactory.getInstance().createIRI(RDF.NAMESPACE, localName);
	}
}

