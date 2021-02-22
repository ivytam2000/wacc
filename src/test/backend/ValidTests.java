package backend;

import static frontend.TestUtilities.exitsWith;

import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ValidTests {

  String validFolderPath = "src/test/examples/valid/";

  @Test
  public void validAdvancedTests() throws IOException {
    exitsWith(validFolderPath + "advanced/", 0);
  }

  @Test
  public void validArrayTests() throws IOException {
    exitsWith(validFolderPath + "array/", 0);
  }

  @Test
  public void validBasicExitTests() throws IOException {
    exitsWith(validFolderPath + "basic/exit/", 0);
  }

  @Test
  public void validBasicSkipTests() throws IOException {
    exitsWith(validFolderPath + "basic/skip/", 0);
  }

  @Test
  public void validExprTests() throws IOException {
    exitsWith(validFolderPath + "expressions/", 0);
  }

  @Test
  public void validNestedFuncTest() throws IOException {
    exitsWith(validFolderPath + "function/nested_functions/", 0);
  }

  @Test
  public void validSimpleFuncTest() throws IOException {
    exitsWith(validFolderPath + "function/simple_functions/", 0);
  }

  @Test
  public void validIfTest() throws IOException {
    exitsWith(validFolderPath + "if/", 0);
  }

  @Test
  public void validIOTest() throws IOException {
    exitsWith(validFolderPath + "IO/", 0);
  }

  @Test
  public void validPairsTest() throws IOException {
    exitsWith(validFolderPath + "pairs/", 0);
  }

  @Test
  public void validRuntimeErrTest() throws IOException {
    exitsWith(validFolderPath + "runtimeErr/", 255);
  }

  @Test
  public void validScopeTest() throws IOException {
    exitsWith(validFolderPath + "scope/", 0);
  }

  @Test
  public void validSequenceTest() throws IOException {
    exitsWith(validFolderPath + "sequence/", 0);
  }

  @Test
  public void validVariablesTest() throws IOException {
    exitsWith(validFolderPath + "variables/", 0);
  }

  @Test
  public void validWhileTest() throws IOException {
    exitsWith(validFolderPath + "while/", 0);
  }
}
