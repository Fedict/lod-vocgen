package be.belgif.vocgen;

import com.google.common.base.Charsets;
import freemarker.template.TemplateException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class VocGenTests {
    Main main = new Main();

    @Test
    public void testNoArgs() throws IOException {
        String testName = "testNoArgs";
        String filename = "System.out";
        Path outputFilePath = getOutputFilePath(testName, filename);
        File outputFile = outputFilePath.toFile();
        File dir = outputFile.getParentFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                fail("could not create output directory " + dir.getAbsolutePath());
            }
        }
        PrintStream outContent = new PrintStream(outputFile);
        PrintStream originalSystemOut = System.out;
        System.setOut(new PrintStream(outContent));
        try {
            assertThrows(MissingOptionException.class, () -> main.generateVocabulary(new String[] {}));
            assertFileEqualsExpected(testName, filename);
        } finally {
            System.setOut(originalSystemOut);
        }
    }

    @Test
    public void testBasic() throws TemplateException, ParseException, IOException {
        String testName = "testBasic";
        main.generateVocabulary(new String[] {
                        "--file", "src/test/resources/rdf.ttl",
                        "--template", "rdf4j",
                        "--long", "The RDF vocabulary",
                        "--short", "RDF",
                        "--ns", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                        "--prefix", "rdf",
                        "--doc", "https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/",
                        "--author", "The Author",
                        "--package", "org.w3.vocab",
                        "--output-dir", testOutputDir(testName),
        });
        assertFileEqualsExpected(testName, "RDF.java");
    }

    @Test
    public void testBasic_Jena() throws TemplateException, ParseException, IOException {
        String testName = "testBasic_Jena";
        main.generateVocabulary(new String[] {
                        "--file", "src/test/resources/rdf.ttl",
                        "--template", "jena",
                        "--long", "The RDF vocabulary",
                        "--short", "RDF",
                        "--ns", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                        "--prefix", "rdf",
                        "--doc", "https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/",
                        "--author", "The Author",
                        "--package", "org.w3.vocab",
                        "--output-dir", testOutputDir(testName),
        });
        assertFileEqualsExpected(testName, "RDF.java");
    }

    @Test
    public void testBasic_Plain() throws TemplateException, ParseException, IOException {
        String testName = "testBasic_Plain";
        main.generateVocabulary(new String[] {
                        "--file", "src/test/resources/rdf.ttl",
                        "--template", "plain",
                        "--long", "The RDF vocabulary",
                        "--short", "RDF",
                        "--ns", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                        "--prefix", "rdf",
                        "--doc", "https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/",
                        "--author", "The Author",
                        "--package", "org.w3.vocab",
                        "--output-dir", testOutputDir(testName),
        });
        assertFileEqualsExpected(testName, "RDF.java");
    }

    @Test
    public void testBasic_SearchClasspath() throws TemplateException, ParseException, IOException {
        String testName = "testBasic_SearchClasspath";
        main.generateVocabulary(new String[] {
                        "--file", "rdf.ttl",
                        "--searchClasspath",
                        "--template", "rdf4j",
                        "--long", "The RDF vocabulary",
                        "--short", "RDF",
                        "--ns", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                        "--prefix", "rdf",
                        "--doc", "https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/",
                        "--author", "The Author",
                        "--package", "org.w3.vocab",
                        "--output-dir", testOutputDir(testName),
        });
        assertFileEqualsExpected(testName, "RDF.java");
    }

    @Test
    public void testBasic_Copyright() throws TemplateException, ParseException, IOException {
        String testName = "testBasic_Copyright";
        main.generateVocabulary(new String[] {
                        "--copyright", "src/main/resources/copyright/rdf4j.txt",
                        "--file", "src/test/resources/rdf.ttl",
                        "--template", "rdf4j",
                        "--long", "The RDF vocabulary",
                        "--short", "RDF",
                        "--ns", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                        "--prefix", "rdf",
                        "--doc", "https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/",
                        "--author", "The Author",
                        "--package", "org.w3.vocab",
                        "--output-dir", testOutputDir(testName),
        });
        assertFileEqualsExpected(testName, "RDF.java");
    }

    @Test
    public void testBasic_Copyright_SearchClasspath() throws TemplateException, ParseException, IOException {
        String testName = "testBasic_Copyright_SearchClasspath";
        main.generateVocabulary(new String[] {
                        "--copyright", "copyright/rdf4j.txt",
                        "--searchClasspath",
                        "--file", "src/test/resources/rdf.ttl",
                        "--template", "rdf4j",
                        "--long", "The RDF vocabulary",
                        "--short", "RDF",
                        "--ns", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                        "--prefix", "rdf",
                        "--doc", "https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/",
                        "--author", "The Author",
                        "--package", "org.w3.vocab",
                        "--output-dir", testOutputDir(testName),
        });
        assertFileEqualsExpected(testName, "RDF.java");
    }

    @Test
    public void testBasic_NoLabels() throws TemplateException, ParseException, IOException {
        String testName = "testBasic_NoLabels";
        main.generateVocabulary(new String[] {
                        "--searchClasspath",
                        "--file", "src/test/resources/rdf-nolabels.ttl",
                        "--template", "rdf4j",
                        "--long", "The RDF vocabulary",
                        "--short", "RDF",
                        "--ns", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                        "--prefix", "rdf",
                        "--doc", "https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/",
                        "--author", "The Author",
                        "--package", "org.w3.vocab",
                        "--output-dir", testOutputDir(testName),
        });
        assertFileEqualsExpected(testName, "RDF.java");
    }

    private String testOutputDir(String testName) {
        return "target/test-output/" + testName;
    }

    private void assertFileEqualsExpected(String directory, String filename) throws IOException {
        Path expectedFilePath = getExpectedFilePath(directory, filename);
        File expectedFile = expectedFilePath.toFile();
        assertTrue(expectedFile::exists, "expected file " + expectedFile.getAbsolutePath() + " does not exist");
        String expectedFileContent = Files.readString(expectedFilePath, Charsets.UTF_8);
        Path outputFilePath = getOutputFilePath(directory, filename);
        File outputFile = outputFilePath.toFile();
        assertTrue(outputFile::exists, "output file " + outputFile.getAbsolutePath() + " does not exist");
        String actualFileContent = Files.readString(outputFilePath, Charsets.UTF_8);
        assertEquals(
                        format(expectedFileContent),
                        format(actualFileContent),
                        "Output file " + outputFile.getAbsolutePath() + " content differs from expected content in "
                                        + expectedFile.getAbsolutePath());
    }

    private Path getOutputFilePath(String directory, String filename) {
        return Path.of("target/test-output/" + directory + "/" + filename);
    }

    private Path getExpectedFilePath(String directory, String filename) {
        return Path.of("src/test/resources/expected/" + directory + "/" + filename);
    }

    /**
     * Remove any IDE formatting we might have introduced by managing the expected file in the IDE.
     *
     * @param input the generated file's content.
     * @return the content, hopefully formatted in such a way that comparing output and expected output shows only interesting differences.
     */
    private String format(String input) {
        return input.replaceAll("[\\s]+\n", "\n").replaceAll("\n[\\s\\t]+(\\S)", "\n    $1");
    }
}

