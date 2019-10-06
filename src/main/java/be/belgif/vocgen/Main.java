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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

/**
 * Quick Vocabulary class generator for Eclipse RDF4j
 *
 * @author Bart.Hanssens
 */
public class Main {
	private static Set<Resource> owlClasses;
	private static Set<Resource> owlProperties;
	private static Set<Resource> owlIndivs;

	/**
	 * Option builder
	 * 
	 * @param c short option name
	 * @param s long option name
	 * @param desc description
	 * @return option
	 */
	private static Option opt(String c, String s, String desc) {
		return Option.builder(c).longOpt(s).required().hasArg().desc(desc).build();
	}
	
	/**
	 * Command line options
	 */
	private static final Options OPTS = new Options()
			.addOption(opt("f", "file", "OWL vocabulary file in TTL format"))
			.addOption(opt("d", "doc", "Documentation URL"))
			.addOption(opt("a", "author", "Name of the java class author"))
			.addOption(opt("n", "ns", "Namespace URL"))
			.addOption(opt("s", "short", "Short vocabulary name"))
			.addOption(opt("l", "long", "Long vocabulary name"))
			.addOption(opt("p", "prefix", "Namespace prefix"));


	/**
	 * Get data for template from command line
	 * 
	 * @param cmd command line
	 * @return map with common data
	 */
	private static Map getData(CommandLine cmd) {
		Map m = new HashMap();
		m.put("author", cmd.getOptionValue('a'));
		m.put("fullname", cmd.getOptionValue('l'));
		m.put("url", cmd.getOptionValue('d'));
		m.put("nsAlias", cmd.getOptionValue('s'));
		m.put("prefix", cmd.getOptionValue('p'));
		return m;
	}
		
	/**
	 * Capitalize and transform string to a valid constant for RDF4J
	 * 
	 * @param s name of the class / property
	 * @return normalized string
	 */
	private static String rdf4jConstants(String s) {
		// namespace and prefix are already used in RDF4J vocabulary class
		if (s.equals("namespace") || s.equals("prefix")) {
			return s.toUpperCase() + "_PROP";
		}
		return s.replaceFirst("^_", "")
				.replaceAll("-", "_")
				.replaceAll("([a-z]+)([A-Z])", "$1_$2")
				.toUpperCase();
	}

	/**
	 * Get local (without namespace) class names mapped to constants for RDF4J 
	 * 
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @return map with class names and constants
	 */
	private static SortedMap<String,String> getRdf4jClasses(Model m, String base) { 
		SortedMap<String,String> classes = new TreeMap();
		getClasses(m, base).forEach(c -> classes.put(c, rdf4jConstants(c)));
		return classes;
	}

	/**
	 * Get local (without namespace) properties mapped to constants for RDF4J 
	 * 
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @param classes 
	 * @return map with properties and constants
	 */
	private static SortedMap<String,String> getRdf4jProps(Model m, String base, 
															SortedMap<String,String> classes) { 
		SortedMap<String,String> props = new TreeMap();
		// prevent duplicates when uppercasing property "name" and class "Name" to NAME
		getProps(m, base).forEach(p -> { 
					String cte = rdf4jConstants(p);
					String key = classes.containsValue(cte) ? cte + "_PROP" : cte;
					props.put(p, key); 
		});
		return props;
	}

	/**
	 * Get local (without namespace) individuals mapped to constants for RDF4J 
	 * 
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @return map with individuals
	 */
	private static SortedMap<String,String> getRdf4jIndivs(Model m, String base,
			SortedMap<String,String> classes, SortedMap<String,String> props) { 
		SortedMap<String,String> indivs = new TreeMap();
		// prevent duplicates when uppercasing property "name" and class "Name" to NAME
		getIndivs(m, base).forEach(p -> { 
					String cte = rdf4jConstants(p);
					String key = (classes.containsValue(cte) || props.containsValue(cte)) ? cte + "_INDIV" : cte;
					indivs.put(p, key); 
		});
		return indivs;
	}
	
	/**
	 * Get a set of local (without namespace) class names
	 * 
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @return set of local class names
	 */
	private static Set<String> getClasses(Model m, String base) {
		owlClasses = m.filter(null, RDF.TYPE, OWL.CLASS).subjects();
		owlClasses.addAll(m.filter(null, RDF.TYPE, RDFS.CLASS).subjects());
System.err.println(base);
		// discard classes outside namespace
		owlClasses.removeIf(s -> !s.toString().startsWith(base));

		// add subclasses
		owlClasses.addAll(owlClasses.stream()
				.flatMap(s -> m.filter(null, RDFS.SUBCLASSOF, s).subjects().stream())
				.collect(Collectors.toSet()));

		// discard named individuals
		owlClasses.removeAll(m.filter(null, RDF.TYPE, OWL.NAMEDINDIVIDUAL).subjects());

		// discard blank nodes
		owlClasses.removeIf(c -> c instanceof BNode);

		// discard blank nodes and return class names (without prefix)
		return owlClasses.stream()
				.map(c -> c.stringValue().replaceFirst(base, ""))
				.collect(Collectors.toSet());		
	}

	/**
	 * Get a set of local (without namespace) properties
	 * 
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @return set of local property names
	 */
	private static Set<String> getProps(Model m, String base) {
		owlProperties = m.filter(null, RDF.TYPE, OWL.OBJECTPROPERTY).subjects();
		owlProperties.addAll(m.filter(null, RDF.TYPE, OWL.DATATYPEPROPERTY).subjects());
		owlProperties.addAll(m.filter(null, RDF.TYPE, RDF.PROPERTY).subjects());

		// add subproperties
		owlProperties.addAll(owlProperties.stream()
				.flatMap(s -> m.filter(null, RDFS.SUBPROPERTYOF, s).subjects().stream())
				.collect(Collectors.toSet()));
	
		return owlProperties.stream()
						.map(p -> p.stringValue().replaceFirst(base, ""))
						.collect(Collectors.toSet());
	}

	/**
	 * Get a set of individuals (without namespace)
	 * 
	 * @param m RDF Model
	 * @param base namespace URI as string
	 */
	private static Set<String> getIndivs(Model m, String base) {
		owlIndivs = m.filter(null, RDF.TYPE, OWL.NAMEDINDIVIDUAL).subjects();
		owlIndivs.addAll(m.filter(null, RDF.TYPE, OWL.INDIVIDUAL).subjects());

		// check for subclasses derived from other classes in this ontology
		owlIndivs.addAll(owlClasses.stream()
				.flatMap(s -> m.filter(null, RDF.TYPE, s).subjects().stream())
				.collect(Collectors.toSet()));

		// discard blank nodes
		owlIndivs.removeIf(c -> c instanceof BNode);
		
		// return indiv names (without prefix)
		return owlIndivs.stream()
						.map(c -> c.stringValue().replaceFirst(base, ""))
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
	 * Write output for Rdf4J or Jena
	 * 
	 * @param cfg freemarker configuration
	 * @param proj project: jena or rdf4j
	 * @param map template data
	 * @throws IOException
	 * @throws TemplateException
	 */
	private static void source(Configuration cfg, String proj, Map map) throws IOException, TemplateException {
		Template ftl = cfg.getTemplate(proj + ".ftl");
		try (Writer out = new FileWriter( proj + ".java")) {
			ftl.process(map, out);
		}
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
	 * Write java class for RDF4J
	 * 
	 * @param cfg freemarker configuration
	 * @param m model
	 * @param base namespace URI as string
	 * @param root template data
	 * @throws IOException
	 * @throws TemplateException
	 */
	private static void writeRdf4j(Configuration cfg, Model m, String base, Map root) 
													throws IOException, TemplateException { 
		SortedMap<String,String> classes = getRdf4jClasses(m, base);
		SortedMap<String,String> props = getRdf4jProps(m, base, classes);
		SortedMap<String,String> indivs = getRdf4jIndivs(m, base, classes, props);
		
		root.put("classMap", classes);
		root.put("propMap", props);
		root.put("indivMap", indivs);
		
		source(cfg, "rdf4j", root);
	}

	/**
	 * Main
	 * 
	 * @param args 
	 * @throws java.io.IOException 
	 * @throws freemarker.template.TemplateException 
	 */
	public static void main(String[] args) throws IOException, TemplateException {	
		CommandLine cmd = null;
		
		try {
			CommandLineParser parser = new DefaultParser();
			cmd = parser.parse(OPTS, args);
		} catch (ParseException ex) {
			HelpFormatter help = new HelpFormatter();
			help.printHelp("VocabGen", OPTS);
			System.exit(-1);
		}
	 
		String owl = cmd.getOptionValue('f');
		String base = cmd.getOptionValue('n'); // namespace URI
		
		Model m = getModel(owl, base);
		
		Set<String> deprecated = getDeprecated(m, base);

		// Template
		Configuration cfg = getConfig();
	
		Map root = getData(cmd);
		root.put("nsURL", base);
		root.put("depr", deprecated);
		
		writeRdf4j(cfg, m, base, root);
	}
}