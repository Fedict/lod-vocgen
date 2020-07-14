/**
 * Copyright (c) 2020 Eclipse RDF4J contributors, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.eclipse.rdf4j.model.vocabulary;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;


/**
 * Constants for the ${fullname}.
 *
 * @see <a href="${url}">${fullname}</a>
 *
 * @author ${author}
 */
public class ${nsAlias} {
	/**
	 * The ${nsAlias} namespace: ${nsURL}
	 */
	public static final String NAMESPACE = "${nsURL}";

	/**
	 * Recommended prefix for the namespace: "${prefix}"
	 */
	public static final String PREFIX = "${prefix}";

	/**
	 * An immutable {@link Namespace} constant that represents the namespace.
	 */
	public static final Namespace NS = new SimpleNamespace(PREFIX, NAMESPACE);

	// Classes
	<#list classMap as class, const>
	/** <tt>${prefix}:${class}</tt> */
	<#if depr?seq_contains(class)>
	@Deprecated
	</#if>
	public static final IRI ${const} = create(${class});

	</#list>

	// Properties
	<#list propMap as prop, const>
	/** <tt>${prefix}:${prop}</tt> */
	<#if depr?seq_contains(prop)>
	@Deprecated
	</#if>
	public static final IRI ${const} = create(${prop});

	</#list>

	// Individuals
	<#list indivMap as indiv, const>
	/** <tt>${prefix}:${indiv}</tt> */
	<#if depr?seq_contains(indiv)>
	@Deprecated
	</#if>
	public static final IRI ${const} = create(${indiv});

	</#list>

	private static IRI create(String localName) {
		return SimpleValueFactory.getInstance().createIRI(${nsAlias}.NAMESPACE, localName);
	}
}

