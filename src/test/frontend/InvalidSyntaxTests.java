package frontend;

import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

import static frontend.TestUtilities.buildFrontEndAnalyser;
import static frontend.TestUtilities.exitsWith;
import static org.junit.Assert.*;

public class InvalidSyntaxTests {

  String invalidSyntaxFolderPath = "src/test/examples/invalid/syntaxErr/";

  // Checks that the syntax error printed is correct.
  private void invalidSyntaxTester(String testName, String expectedError) throws IOException {
    String sourceFilePath = invalidSyntaxFolderPath + testName;
    OutputStream os = new ByteArrayOutputStream();
    System.setOut(new PrintStream(os));
    buildFrontEndAnalyser(sourceFilePath).run();
    // assert that correct error message is printed
    assertTrue(os.toString().contains(expectedError));
  }

  // Exit code Tests
  @Test
  public void invalidSyntaxArrayTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "array/", 100);
  }

  @Test
  public void invalidSyntaxBasicTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "basic/", 100);
  }

  @Test
  public void invalidSyntaxExprTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "expressions/", 100);
  }

  /* functionJunkAfterReturn does not exit with 100 because it did not detect
   * that the function did not end with a return or exit statement - semantic
   * checker should detect? */
  @Test
  public void invalidSyntaxFunctionTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "function/", 100);
  }

  @Test
  public void invalidSyntaxIfTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "if/", 100);
  }

  @Test
  public void invalidSyntaxPairsTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "pairs/", 100);
  }

  @Test
  public void invalidSyntaxPrintTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "print/", 100);
  }

  @Test
  public void invalidSyntaxSequenceTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "sequence/", 100);
  }

  /* bigIntAssignment does not exit with 100 because it did not detect the
   * badly formatted integer - semantic checker should detect? */
  @Test
  public void invalidSyntaxVariablesTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "variables/", 100);
  }

  @Test
  public void invalidSyntaxWhileTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "while/", 100);
  }

  // Tests to test that correct syntax error was printed
  @Test
  public void basicBadCommentTest() throws IOException {
    invalidSyntaxTester(
        "basic/badComment.wacc", "Syntactic error at line " + "12:6 -- " + "mismatched input ','");
  }

  @Test
  public void basicBadComment2Test() throws IOException {
    invalidSyntaxTester(
        "basic/badComment2.wacc", "Syntactic error at line " + "12:19" + " -- missing '=' at 'I'");
    invalidSyntaxTester(
        "basic/badComment2.wacc",
        "Syntactic error at " + "line 12:55 -- token recognition error at: '?'");
  }

  @Test
  public void basicBadEscapeTest() throws IOException {
    invalidSyntaxTester(
        "basic/badEscape.wacc",
        "Syntactic error at line " + "12:11 -- token recognition error at: ''\\H'");
  }
}
