package frontend;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static frontend.TestUtilities.exitsWith;

public class InvalidSemanticTests {

  String invalidSemanticFolderPath = "src/test/examples/invalid/semanticErr/";

  @Test
  public void invalidSemanticExitTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "exit/", 200);
  }

  @Test
  public void invalidSemanticExprTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "expressions/", 200);
  }


  @Test
  public void invalidSemanticFunctionTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "function/", 200);
  }

  @Test
  public void invalidSemanticIfTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "if/", 200);
  }

  @Test
  public void invalidSemanticIOTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "IO/", 200);
  }

  @Test
  public void invalidSemanticMultipleTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "multiple/", 200);
  }

  @Test
  public void invalidSemanticPairsTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "pairs/", 200);
  }

  @Test
  public void invalidSemanticPrintTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "print/", 200);
  }

  @Test
  public void invalidSemanticReadTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "read/", 200);
  }

  @Test
  public void invalidSemanticScopeTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "scope/", 200);
  }

  @Test
  public void invalidSemanticVariablesTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "variables/", 200);
  }

  @Test
  public void invalidSemanticWhileTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "while/", 200);
  }

  @Test
  public void invalidSemanticClassTests() throws IOException {
    exitsWith(invalidSemanticFolderPath + "extension/class/", 200);
  }
}
