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
import org.apache.commons.cli.*;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * Quick Vocabulary class generator for Eclipse RDF4j
 *
 * @author Bart.Hanssens
 */
public class Main {
	private Set<Resource> owlClasses;
	private Set<Resource> owlProperties;
	private Set<Resource> owlIndivs;

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
			.addOption(Option.builder("a").longOpt("author").hasArg().desc( "Name of the java class author").required(false).build())
			.addOption(opt("n", "ns", "Namespace URL"))
			.addOption(opt("s", "short", "Short vocabulary name"))
			.addOption(opt("l", "long", "Long vocabulary name"))
			.addOption(opt("p", "prefix", "Namespace prefix"))
			.addOption(opt("t", "template", "one of: rdf4j, jena, plain"))
			.addOption(Option.builder("sc").longOpt("snake-case").desc("use all caps snake case constants instead of as-is local names").required(false).build())
			.addOption(Option.builder("jp").longOpt("package").hasArg().desc( "java package").build())
			.addOption(Option.builder("o").longOpt("output-dir").required(false).hasArg().desc( "output directory").build())
			.addOption(Option.builder("c").longOpt("copyright").hasArg().required(false).desc("file containing the copyright snippet").build())
			.addOption(Option.builder("cp").longOpt("searchClasspath").hasArg(false).desc("look for input files on classpath, then in filesystem").required(false).build());




	/**
	 * Get data for template from command line
	 *
	 * @param cmd command line
	 * @return map with common data
	 */
	private static Map getData(CommandLine cmd) {
		Map m = new HashMap();
		if (cmd.hasOption("a")){
			m.put("author", cmd.getOptionValue('a'));
		}
		m.put("fullname", cmd.getOptionValue('l'));
		m.put("url", cmd.getOptionValue('d'));
		m.put("nsAlias", cmd.getOptionValue('s'));
		m.put("prefix", cmd.getOptionValue('p'));
		return m;
	}

	/**
	 * Capitalize and transform string to a valid constant for RDF4J, i.e. ALL_CAPS_SNAKE_CASE
	 *
	 * @param s name of the class / property
	 * @return normalized string
	 */
	private static String snakeCase(String s) {
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
	 * Get local (without namespace) class names mapped to constants for RDF4J , i.e. ALL_CAPS_SNAKE_CASE
	 *
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @return map with class names and constants
	 */
	private Map<String,Constant> getSnakeCaseClasses(Model m, String base) {
		Map<String,Constant> classes = new TreeMap<>();
		getClasses(m, base).forEach(c -> classes.put(c.getName(), new Constant(snakeCase(c.getName()), c.getLabel())));
		return classes;
	}


	/**
	 * Get local (without namespace) properties mapped to constants for RDF4J, i.e. ALL_CAPS_SNAKE_CASE
	 *
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @param classes
	 * @return map with properties and constants
	 */
	private Map<String,Constant> getSnakeCaseProps(Model m, String base,
															Map<String,Constant> classes) {
		Map<String,Constant> props = new TreeMap<>();
		// prevent duplicates when uppercasing property "name" and class "Name" to NAME
		getProps(m, base).forEach(p -> {
					String cte = snakeCase(p.getName());
					String key = classes.containsValue(cte) ? cte + "_PROP" : cte;
					props.put(p.getName(), new Constant(key, p.getLabel()));
		});
		return props;
	}

	/**
	 * Get local (without namespace) individuals mapped to constants for RDF4J, i.e. ALL_CAPS_SNAKE_CASE.
	 *
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @return map with individuals
	 */
	private Map<String,Constant> getSnakeCaseIndivs(Model m, String base,
			Map<String,Constant> classes, Map<String,Constant> props) {
		Map<String,Constant> indivs = new TreeMap<>();
		// prevent duplicates when uppercasing property "name" and class "Name" to NAME
		getIndivs(m, base).forEach(p -> {
					String cte = snakeCase(p.getName());
					String key = (classes.containsValue(cte) || props.containsValue(cte)) ? cte + "_INDIV" : cte;
					indivs.put(p.getName(), new Constant(key, p.getLabel()));
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
	private Set<Constant> getClasses(Model m, String base) {
		owlClasses = m.filter(null, RDF.TYPE, OWL.CLASS).subjects();
		owlClasses.addAll(m.filter(null, RDF.TYPE, RDFS.CLASS).subjects());

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
				.map(c -> new Constant(c.stringValue().replaceFirst(base, ""), getLabel(m, c)))
				.collect(Collectors.toSet());
	}

	private String getLabel(Model m, Resource resource) {
		Set<Value> labels =m.filter(resource, RDFS.LABEL, null).objects();
		if (labels.isEmpty()) {
			return null;
		}
		if (labels.size() == 1) {
			return labels.stream().map(Value::stringValue).findFirst().get();
		}
		return labels.stream().map(Value::stringValue).collect(joining(" or "));
	}

	/**
	 * Get a set of local (without namespace) properties
	 *
	 * @param m RDF Model
	 * @param base namespace URI as string
	 * @return set of local property names
	 */
	private Set<Constant> getProps(Model m, String base) {
		owlProperties = m.filter(null, RDF.TYPE, OWL.OBJECTPROPERTY).subjects();
		owlProperties.addAll(m.filter(null, RDF.TYPE, OWL.DATATYPEPROPERTY).subjects());
		owlProperties.addAll(m.filter(null, RDF.TYPE, RDF.PROPERTY).subjects());

		// add subproperties
		owlProperties.addAll(owlProperties.stream()
				.flatMap(s -> m.filter(null, RDFS.SUBPROPERTYOF, s).subjects().stream())
				.collect(Collectors.toSet()));

		return owlProperties.stream()
						.filter(p -> p.stringValue().startsWith(base)) // only use properties from the base namespace
						.map(c -> new Constant(c.stringValue().replaceFirst(base, ""), getLabel(m, c)))
						.collect(Collectors.toSet());
	}

	/**
	 * Get a set of individuals (without namespace)
	 *
	 * @param m RDF Model
	 * @param base namespace URI as string
	 */
	private Set<Constant> getIndivs(Model m, String base) {
		owlIndivs = m.filter(null, RDF.TYPE, OWL.NAMEDINDIVIDUAL).subjects();
		owlIndivs.addAll(m.filter(null, RDF.TYPE, OWL.INDIVIDUAL).subjects());

		// check for subclasses derived from other classes in this ontology
		owlIndivs.addAll(owlClasses.stream()
				.flatMap(s -> m.filter(null, RDF.TYPE, s).subjects().stream())
				.collect(Collectors.toSet()));

        // avoid duplication
        owlIndivs.removeAll(owlClasses);
        owlIndivs.removeAll(owlProperties);

		// discard blank nodes
		owlIndivs.removeIf(c -> c instanceof BNode);

		// return indiv names (without prefix)
		return owlIndivs.stream()
						.filter(p -> p.stringValue().startsWith(base)) // only use individuals from the base namespace
						.map(c -> new Constant(c.stringValue().replaceFirst(base, ""), getLabel(m, c)))
						.collect(Collectors.toSet());
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

		Set<Resource> deprecated = m.filter(null, OWL.DEPRECATEDCLASS, tr).subjects();
		deprecated.addAll(m.filter(null, OWL.DEPRECATEDPROPERTY, tr).subjects());
		deprecated.addAll(m.filter(null, OWL.DEPRECATED, tr).subjects());

		return deprecated.stream()
						.map(d -> d.stringValue().replaceFirst(base, ""))
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
	private static Model getModel(String file, String base, boolean searchForFileOnClasspath) throws IOException {
		if (searchForFileOnClasspath) {
			try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file)) {
				if (in != null) {
					RDFFormat fmt = Rio.getParserFormatForFileName(file).orElse(RDFFormat.TURTLE);
					return Rio.parse(in, base, fmt);
				}
			}
		}
		InputStream in = new FileInputStream(file);
		RDFFormat fmt = Rio.getParserFormatForFileName(file).orElse(RDFFormat.TURTLE);
		return Rio.parse(in, base, fmt);
	}

	/**
	 * Write output for Rdf4J or Jena
	 *
	 * @param cfg freemarker configuration
	 * @param template project: jena or rdf4j
	 * @param map template data
	 * @throws IOException
	 * @throws TemplateException
	 */
	private static void source(Configuration cfg, TemplateType template, Map map, File outputDir) throws IOException, TemplateException {
		Template ftl = cfg.getTemplate(template.toString().toLowerCase() + ".ftl");
		String className = (String) map.get("nsAlias");
		if (!outputDir.exists()) {
			System.out.println("creating output directory " + outputDir.getAbsolutePath());
			if (!outputDir.mkdirs()) {
				throw new IOException("Unable to create output directory");
			}
		}
		File outFile = new File(outputDir, className + ".java");
		try (Writer out = new FileWriter(outFile)) {
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
	private void writeVocab(Configuration cfg, Model m, String base, Map root, File outputDir, boolean snakeCase, TemplateType template)
													throws IOException, TemplateException {
		Map<String,Constant> classes = snakeCase
						? getSnakeCaseClasses(m, base)
						: getSafeNameMap(getClasses(m, base));
		Map<String,Constant> props = snakeCase
						? getSnakeCaseProps(m, base, classes)
						: getSafeNameMap(getProps(m, base));
		Map<String,Constant> indivs = snakeCase
						? getSnakeCaseIndivs(m, base, classes, props)
						: getSafeNameMap(getIndivs(m, base));

		setDefaultLabelsIfMissing(classes, (String) root.get("prefix"));
		setDefaultLabelsIfMissing(props, (String) root.get("prefix"));
		setDefaultLabelsIfMissing(indivs, (String) root.get("prefix"));

		root.put("classMap", classes);
		root.put("propMap", props);
		root.put("indivMap", indivs);

		source(cfg, template, root, outputDir);
	}

	private void setDefaultLabelsIfMissing(Map<String, Constant> constantMap, String nsPrefix) {
		constantMap.values()
						.stream()
						.forEach(c -> {
							if (c.getLabel() == null) {
								c.setLabel(nsPrefix + ":" + c.getName());
							}
						});
	}

	private static Map<String, Constant> getSafeNameMap(Set<Constant> localNames) {
		return new TreeMap<>(localNames.stream()
						.collect(Collectors.toMap(c -> c.getName(), c -> new Constant(toSafeJavaName(c.getName()), c.getLabel()))));
	}

	private static Pattern javaLanguageKeywords = Pattern.compile(
		"(abstract|continue|for|new|switch|assert|default|goto|package|synchronized|boolean|do|if|private|this|"
						+ "break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|"
						+ "instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|"
						+ "void|class|finally|long|strictfp|volatile|const|float|native|super|while)"
	);

	private static String toSafeJavaName(String localName){
		Matcher m = javaLanguageKeywords.matcher(localName);
		if (m.matches()){
			return localName + "_";
		}
		return localName
						.replaceAll("^_", "")
						.replaceAll("[+\\-~*#&%§!]", "_");
	}




	/**
	 * Main
	 *
	 * @param args
	 * @throws java.io.IOException
	 * @throws freemarker.template.TemplateException
	 */
	public static void main(String[] args) throws IOException, TemplateException {
		Main main = new Main();
		try {
			main.generateVocabulary(args);
		} catch (ParseException ex) {
			System.exit(-1);
		}


	}

	public void generateVocabulary(String[] args) throws ParseException, IOException, TemplateException {
		CommandLine cmd;
		CommandLineParser parser = new DefaultParser();
		try {
			cmd = parser.parse(OPTS, args);
		} catch (ParseException e) {
			HelpFormatter help = new HelpFormatter();
			System.out.println(e.getMessage());
			help.printHelp("VocabGen", OPTS);
			throw e;
		}
		String ontologyFile = cmd.getOptionValue('f');
		String base = cmd.getOptionValue('n'); // namespace URI
		String javaPackage = Optional.ofNullable(cmd.getOptionValue("jp")).orElse("org.eclipse.rdf4j.model.vocabulary");
		String outputDirStr = Optional.ofNullable(cmd.getOptionValue("o")).orElse(".");
		boolean snakeCase = cmd.hasOption("sc");
		File outputDir = new File(outputDirStr);
		boolean searchFilesOnClasspath = cmd.hasOption("cp");
		String copyrightFileName = cmd.getOptionValue("c");
		String copyright = null;
		copyright = getCopyright(copyrightFileName, copyright, searchFilesOnClasspath);
		TemplateType template = TemplateType.valueOf(cmd.getOptionValue("t").toUpperCase());
		Model m = getModel(ontologyFile, base, searchFilesOnClasspath);
		Set<String> deprecated = getDeprecated(m, base);
		// Template
		Configuration cfg = getConfig();
		Map root = getData(cmd);
		root.put("nsURL", base);
		root.put("depr", deprecated);
		root.put("package", javaPackage);
		root.put("copyright", copyright);
		writeVocab(cfg, m, base, root, outputDir, snakeCase, template);
	}

	private String getCopyright(String copyrightFileName, String copyright, boolean searchFilesOnClasspath) throws IOException {
		if (copyrightFileName != null){
			if (searchFilesOnClasspath) {
				try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(copyrightFileName)) {
					if (in != null) {
						return new String(in.readAllBytes(), StandardCharsets.UTF_8);
					}
				}
			}
			return Files.readString(Path.of(copyrightFileName), StandardCharsets.UTF_8);
		}
		return null;
	}

	private static enum TemplateType {
		RDF4J, JENA, PLAIN
	}
}