/**
 * Copyright (c) 2017 Eclipse RDF4J contributors, and others.
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
	/** ${prefix}:${class} */
	<#if depr?seq_contains(class)>
	@Deprecated
	</#if>
	public static final IRI ${const};

	</#list>

	// Properties
	<#list propMap as prop, const>
	/** ${prefix}:${prop} */
	<#if depr?seq_contains(prop)>
	@Deprecated
	</#if>
	public static final IRI ${const};

	</#list>

	// Individuals
	<#list indivMap as indiv, const>
	/** ${prefix}:${indiv} */
	<#if depr?seq_contains(indiv)>
	@Deprecated
	</#if>
	public static final IRI ${const};

	</#list>

	static {
		ValueFactory factory = SimpleValueFactory.getInstance();

		<#list classMap as class, const>
		${const} = factory.createIRI(NAMESPACE, "${class}");
		</#list>

		<#list propMap as prop, const>
		${const} = factory.createIRI(NAMESPACE, "${prop}");
		</#list>

		<#list indivMap as indiv, const>
		${const} = factory.createIRI(NAMESPACE, "${indiv}");
		</#list>
	}
}

