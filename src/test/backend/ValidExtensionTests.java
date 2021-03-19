package backend;

import static backend.TestUtilities.executablesFromOurCompilerMatchesReferenceCompiler;

import java.io.IOException;
import org.junit.Test;

public class ValidExtensionTests {
  // Made test cases for extension: Bit-wise Operators '&', '|' and '~'
  @Test
  public void validExtensionBitwiseTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        TestUtilities.EXT_VALID_DIR + "bitwise/");
  }

  // Made test cases for extension: Binary, Octal and Hexadecimal literals
  @Test
  public void validExtensionIntegerLiteralTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        TestUtilities.EXT_VALID_DIR + "integerLiterals/");
  }

  // Made test cases for extension: Additional Loop Control Statements

  @Test
  public void validExtensionForLoopTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        TestUtilities.EXT_VALID_DIR + "forLoop/");
  }

  // Made test cases for extension: Classes
  @Test
  public void validExtensionForClassesTest() throws IOException {
    executablesFromOurCompilerMatchesReferenceCompiler(
        TestUtilities.EXT_VALID_DIR + "class/");
  }
}
