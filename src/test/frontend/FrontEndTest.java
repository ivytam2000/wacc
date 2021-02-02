package frontend;

import java.io.FileInputStream;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.Test;

import static org.junit.Assert.*;

public class FrontEndTest {

  private FrontEndAnalyser buildFrontEndAnalyser(String sourceFilePath)
      throws IOException {
    CharStream source = CharStreams.fromStream(new FileInputStream(sourceFilePath));
    return new FrontEndAnalyser(source);
  }

  @Test
  public void compilesBinarySortTreeSuccessfully() throws IOException {
    String sourceFilePath = "src/test/resources/binarySortTree.wacc";
    assertEquals(buildFrontEndAnalyser(sourceFilePath).run(), 0);
  }

  @Test
  public void compilesHashTableSuccessfully() throws IOException {
    String sourceFilePath = "src/test/resources/hashTable.wacc";
    assertEquals(buildFrontEndAnalyser(sourceFilePath).run(), 0);
  }

  @Test
  public void compilesTicTacToeSuccessfully() throws IOException {
    String sourceFilePath = "src/test/resources/ticTacToe.wacc";
    assertEquals(buildFrontEndAnalyser(sourceFilePath).run(), 0);
  }
}
