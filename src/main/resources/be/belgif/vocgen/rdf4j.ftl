<#if copyright??>
${copyright}
</#if>
package ${package};

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;


/**
 * Constants for the ${fullname}.
 *
 * @see <a href="${url}">${fullname}</a>
 * <#if author??>
 *	@author ${author} </#if>
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
    <#if const.label??>
    /** <code>${const.label}</code> **/
    </#if>
	<#if depr?seq_contains(class)>
	@Deprecated
	</#if>
	public static final IRI ${const.name} = create("${class}");

	</#list>

	// Properties
	<#list propMap as prop, const>
    <#if const.label??>
    /** <code>${const.label}</code> **/
    </#if>
	<#if depr?seq_contains(prop)>
	@Deprecated
	</#if>
	public static final IRI ${const.name} = create("${prop}");

	</#list>

	// Individuals
	<#list indivMap as indiv, const>
    <#if const.label??>
    /** <code>${const.label}</code> **/
    </#if>
	<#if depr?seq_contains(indiv)>
	@Deprecated
	</#if>
	public static final IRI ${const.name} = create("${indiv}");

	</#list>

	private static IRI create(String localName) {
		return SimpleValueFactory.getInstance().createIRI(${nsAlias}.NAMESPACE, localName);
	}
}

