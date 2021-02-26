package backend;

import static backend.TestUtilities.executablesFromOurCompilerMatchesReferenceCompiler;

import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ValidTests {

  String validFolderPath = "src/test/examples/valid/";

  @Test
  public void validAdvancedTests() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "advanced/");
  }

  @Test
  public void validArrayTests() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "array/");
  }

  @Test
  public void validBasicExitTests() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "basic/exit/");
  }

  @Test
  public void validBasicSkipTests() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "basic/skip/");
  }

  @Test
  public void validExprTests() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "expressions/");
  }

  @Test
  public void validNestedFuncTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "function/nested_functions/");
  }

  @Test
  public void validSimpleFuncTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "function/simple_functions/");
  }

  @Test
  public void validIfTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(validFolderPath + "if/");
  }

  @Test
  public void validIOTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(validFolderPath + "IO/");
  }

  @Test
  public void validPairsTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "pairs/");
  }

  @Test
  public void validRuntimeErrTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "runtimeErr/");
  }

  @Test
  public void validScopeTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "scope/");
  }

  @Test
  public void validSequenceTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "sequence/");
  }

  @Test
  public void validVariablesTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "variables/");
  }

  @Test
  public void validWhileTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        validFolderPath + "while/");
  }
}
