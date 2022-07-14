<#if copyright??>
    ${copyright}
</#if>
package ${package};

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Individual;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * Constants for the ${fullname}.
 *
 * @see <a href="${url}">${fullname}</a>
 */
public class ${nsAlias} {
    private static final Model m = ModelFactory.createDefaultModel();

    /**
     * The ${nsAlias} namespace: ${nsURL}
     */
    public static final String NS = "${nsURL}";
    public static final Resource NAMESPACE = m.createResource(NS);

    /**
     * Returns the URI for this schema
     * @return URI
     */
    public static String getURI() {
        return NS;
    }
    
    // Classes
    <#list classMap as class, const>
    <#if const.label??>
    /** <code>${const.label}</code> **/
    </#if>
    <#if depr?seq_contains(class)>
    @Deprecated
    </#if>
    public static final Resource ${const.name} = m.createResource(NS + "${class}");
    </#list>

    // Properties
    <#list propMap as prop, const>
    <#if const.label??>
    /** <code>${const.label}</code> **/
    </#if>
    <#if depr?seq_contains(prop)>
    @Deprecated
    </#if>
    public static final Property ${const.name} = m.createProperty(NS + "${prop}");
    </#list>

    // Individuals
    <#list indivMap as ind, const>
    <#if const.label??>
        /** <code>${const.label}</code> **/
    </#if>
    <#if depr?seq_contains(ind)>
    @Deprecated
    </#if>
    public static final Individual ${const.name} = m.createProperty(NS + "${ind}");
    </#list>
}
