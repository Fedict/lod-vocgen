package be.belgif.vocgen;

import com.google.common.base.Charsets;
import freemarker.template.TemplateException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class VocGenTests {
    Main main = new Main();

    @Test
    public void testNoArgs() {
        assertThrows(MissingOptionException.class, () -> main.generateVocabulary(new String[] {}));
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


    private String testOutputDir(String testName) {
        return "target/test-output/" + testName;
    }

    private void assertFileEqualsExpected(String directory, String filename) throws IOException {
        Path outputFilePath = Path.of("target/test-output/" + directory + "/" + filename);
        Path expectedFilePath = Path.of("src/test/resources/expected/" + directory + "/" + filename);
        File outputFile = outputFilePath.toFile();
        File expectedFile = expectedFilePath.toFile();
        assertTrue(outputFile::exists, "output file " + outputFile.getAbsolutePath() + " does not exist");
        assertTrue(expectedFile::exists, "expected file " + expectedFile.getAbsolutePath() + " does not exist");
        assertEquals(
                        format(Files.readString(expectedFilePath, Charsets.UTF_8)),
                        format(Files.readString(outputFilePath, Charsets.UTF_8)),
                        "Output file " + outputFile.getAbsolutePath() + " content differs from expected content in "
                                        + expectedFile.getAbsolutePath());
    }



    /**
     * Remove any IDE formatting we might have introduced by managing the expected file in the IDE.
     * @param input the generated file's content.
     * @return the content, hopefully formatted in such a way that comparing output and expected output shows only interesting differences.
     */
    private String format(String input){
        return input.replaceAll("[\\s]+\n", "\n").replaceAll("\n[\\s\\t]+(\\S)", "\n    $1");
    }
}

