package frontend;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvalidSyntaxTests {
    String folderPath = "src/test/examples/invalid/syntaxErr"
        + "/basic/";

    private static FrontEndAnalyser buildFrontEndAnalyser(String sourceFilePath)
        throws IOException {
        CharStream source = CharStreams.fromStream(new FileInputStream(sourceFilePath));
        return new FrontEndAnalyser(source);
    }

    private void invalidSyntaxTester(String testName, String expectedError)
        throws IOException {
        String sourceFilePath = folderPath + testName;
        OutputStream os = new ByteArrayOutputStream();
        System.setOut(new PrintStream(os));
        //assert that the exit code is correct
        assertEquals(buildFrontEndAnalyser(sourceFilePath).run(),100);
        //assert that correct error message is printed
        assertTrue(os.toString().contains(expectedError));
    }

    @Test
    public void basicBadCommentTest() throws IOException {
        invalidSyntaxTester("badComment.wacc", "Syntactic error at line 12:6 -- "
            + "mismatched input ','");
    }

    @Test
    public void basicBadComment2Test() throws IOException{
        invalidSyntaxTester("badComment2.wacc","Syntactic error at line 12:19"
            + " -- missing '=' at 'I'");
        invalidSyntaxTester("badComment2.wacc", "Syntactic error at line 12:55 -- token recognition error at: '?'");
    }

    @Test
    public void basicBadEscapeTest() throws IOException {
        //why does it print line 12:55 token recognition error at: '?'
        invalidSyntaxTester("badEscape.wacc","Syntactic error at line 12:11 -- token recognition error at: ''\\H'");
    }

}
