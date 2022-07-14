<#if copyright??>
    ${copyright}
</#if>
package ${package};

/**
 * Constants for the ${fullname}.
 *
 * @see <a href="${url}">${fullname}</a>
 */
public class ${nsAlias} {

    /**
     * The ${nsAlias} namespace: ${nsURL}
     */
    public static final String NS = "${nsURL}";

    /**
     * Returns the URI for this schema
     * @return URI
     */
    public static String getURI() {
        return NS;
    }
    
    // Classes
    <#list classMap as class, const>
    <#if depr?seq_contains(class)>
    @Deprecated
    </#if>
    public static final String ${const} = NS + "${class}";
    </#list>

    // Properties
    <#list propMap as prop, const>
    <#if depr?seq_contains(prop)>
    @Deprecated
    </#if>
    public static final String ${const} = NS + "${prop}";
    </#list>

    // Individuals
    <#list indivMap as ind, const>
    <#if depr?seq_contains(ind)>
    @Deprecated
    </#if>
    public static final String ${ind} = NS + "${ind}";
    </#list>
}
