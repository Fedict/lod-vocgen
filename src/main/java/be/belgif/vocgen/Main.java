/*
 * Copyright (c) 2017, Bart Hanssens <bart.hanssens@fedict.be>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package be.belgif.vocgen;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.eclipse.rdf4j.model.BNode;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

/**
 * Quick Vocabulary class generator for Apache Jena and Eclipse RDF4j
 *
 * @author Bart.Hanssens
 */
public class Main {
	/**
	 * Capitalize and transform string to valid constant
	 * 
	 * @param s name of the class / property
	 * @return normalized string
	 */
	private static String capitalize(String s) {
		return s.replaceFirst("^_", "")
				.replaceAll("-", "_")
				.replaceAll("([a-z]+)([A-Z])", "$1_$2")
				.toUpperCase();
	}
	
	/**
	 * Get a set of local (without namespace) class names
	 * 
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @return set of local class names
	 */
	private static Set<String> getClasses(Model m, String base) {
		Set<Resource> owlClasses = m.filter(null, RDF.TYPE, OWL.CLASS).subjects();
		return owlClasses.stream()
						.filter(c -> !(c instanceof BNode))
						.map(c -> c.stringValue().replaceFirst(base, ""))
						.collect(Collectors.toSet());		
	}
	
	/**
	 * Get a set of local (without namespace) propertues
	 * 
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @return set of local property names
	 */
	private static Set<String> getProps(Model m, String base) {
		Set<Resource> owlProps = m.filter(null, RDF.TYPE, OWL.OBJECTPROPERTY).subjects();
		owlProps.addAll(m.filter(null, RDF.TYPE, OWL.DATATYPEPROPERTY).subjects());
		return owlProps.stream()
						.map(p ->  p.stringValue().replaceFirst(base, ""))
						.collect(Collectors.toSet());
	}
	
	/**
	 * Read an OWL file into and RDF model
	 * 
	 * @param file input file
	 * @param base namespace URI
	 * @return RDF model
	 * @throws IOException 
	 */
	private static Model getModel(String file, String base) throws IOException {
		InputStream in = new FileInputStream(file);
		RDFFormat fmt = Rio.getParserFormatForFileName(file).orElse(RDFFormat.TURTLE);
		return Rio.parse(in, base, fmt);	
	}
	
	/**
	 * Get deprecated classes and properties
	 * 
	 * @param m model
	 * @param base namespace URI as string
	 * @return set of deprecated classes / properties as string
	 */
	private static Set<String> getDeprecated(Model m, String base) {
		SimpleValueFactory f = SimpleValueFactory.getInstance();
		Literal tr = f.createLiteral(true);
		IRI owl2dep = f.createIRI("http://www.w3.org/2002/07/owl#deprecated");
		
		Set<Resource> deprecated = m.filter(null, OWL.DEPRECATEDCLASS, tr).subjects();
		deprecated.addAll(m.filter(null, OWL.DEPRECATEDPROPERTY, tr).subjects());
		deprecated.addAll(m.filter(null, owl2dep, tr).subjects());
		
		return deprecated.stream()
						.map(d -> d.stringValue().replaceFirst(base, ""))
						.collect(Collectors.toSet());
	}
	
	/**
	 * Get Freemarker configuration.
	 * 
	 * @return freemarker configuration 
	 */
	private static Configuration getConfig() {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
		cfg.setClassLoaderForTemplateLoading(Main.class.getClassLoader(), "be/belgif/vocgen");
		cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		
		return cfg;
	}
	
	/**
	 * Main
	 * 
	 * @param args 
	 * @throws java.io.IOException 
	 * @throws freemarker.template.TemplateException 
	 */
	public static void main(String[] args) throws IOException, TemplateException {
		if (args.length < 6) {
			System.err.println("Usage: VocabGen"
					+ " <vocab.ttl> <nsURL> <nsPrefix> <nsAlias> <docURL> <fullname>");
		}
		
		String owl = args[0]; // OWL input file in TTL
		String base = args[1]; // namespace URI
		String prefix = args[2]; // without ":", e.g. vcard
		String alias = args[3]; // eg VCARD4
		String doc = args[4]; // URL with human readable docs
		String fullname = args[5]; // full name of the ontology
		
		Model m = getModel(owl, base);
		
		// Classed and properties
		SortedMap<String,String> classes = new TreeMap();
		getClasses(m, base).forEach(c -> classes.put(c, capitalize(c)));
		
		SortedMap<String,String> props = new TreeMap();
		getProps(m, base).forEach(p -> props.put(p, capitalize(p)));
		
		Set<String> deprecated = getDeprecated(m, base);

		// Load template
		Configuration cfg = getConfig();
		Template rdf4j = cfg.getTemplate("rdf4j.ftl");
		
		// Pass data to template
		Map root = new HashMap();
		root.put("fullname", fullname);
		root.put("url", doc);
		root.put("nsURL", base);
		root.put("prefix", prefix);
		root.put("nsAlias", alias);
		root.put("author", "Bart Hanssens");
		root.put("depr", deprecated);
		root.put("classMap", classes);
		root.put("propMap", props);
		
		try (Writer out = new FileWriter("rdf4j.java")) {
			rdf4j.process(root, out);
		}
	}
}
