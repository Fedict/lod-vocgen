/**
 * Copyright (c) 2020 Eclipse RDF4J contributors, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
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
    /** <code>Alt</code> **/
	public static final IRI Alt = create("Alt");

    /** <code>Bag</code> **/
	public static final IRI Bag = create("Bag");

    /** <code>CompoundLiteral</code> **/
	public static final IRI CompoundLiteral = create("CompoundLiteral");

    /** <code>List</code> **/
	public static final IRI List = create("List");

    /** <code>Property</code> **/
	public static final IRI Property = create("Property");

    /** <code>Seq</code> **/
	public static final IRI Seq = create("Seq");

    /** <code>Statement</code> **/
	public static final IRI Statement = create("Statement");


	// Properties
    /** <code>direction</code> **/
	public static final IRI direction = create("direction");

    /** <code>first</code> **/
	public static final IRI first = create("first");

    /** <code>language</code> **/
	public static final IRI language = create("language");

    /** <code>object</code> **/
	public static final IRI object = create("object");

    /** <code>predicate</code> **/
	public static final IRI predicate = create("predicate");

    /** <code>rest</code> **/
	public static final IRI rest = create("rest");

    /** <code>subject</code> **/
	public static final IRI subject = create("subject");

    /** <code>type</code> **/
	public static final IRI type = create("type");

    /** <code>value</code> **/
	public static final IRI value = create("value");


	// Individuals
    /** <code>nil</code> **/
	public static final IRI nil = create("nil");


	private static IRI create(String localName) {
		return SimpleValueFactory.getInstance().createIRI(RDF.NAMESPACE, localName);
	}
}

