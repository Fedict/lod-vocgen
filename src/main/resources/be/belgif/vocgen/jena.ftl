/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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
    <#if depr?seq_contains(class)>
    @Deprecated
    </#if>
    public static final Resource ${const} = m.createResource(NS + "${class}");
    </#list>

    // Properties
    <#list propMap as prop, const>
    /** ${prefix}:${prop} */
    <#if depr?seq_contains(prop)>
    @Deprecated
    </#if>
    public static final Properrty ${const} = m.createResource(NS + "${prop}");
    </#list>
}
