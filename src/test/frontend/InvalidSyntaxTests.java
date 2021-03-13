package frontend;

import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

import static frontend.TestUtilities.buildFrontEndAnalyser;
import static frontend.TestUtilities.exitsWith;
import static org.junit.Assert.*;

public class InvalidSyntaxTests {

  String invalidSyntaxFolderPath = "src/test/examples/invalid/syntaxErr/";

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

  @Test
  public void invalidSyntaxVariablesTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "variables/", 100);
  }

  @Test
  public void invalidSyntaxWhileTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "while/", 100);
  }

  @Test
  public void invalidSyntaxClassTest() throws IOException {
    exitsWith(invalidSyntaxFolderPath + "extension/class/", 100);
  }

}
