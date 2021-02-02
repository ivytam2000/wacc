package frontend;

import java.io.FileInputStream;
import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.Test;

import static org.junit.Assert.*;

public class FrontEndTest {

  String resourcesFolderPath = "src/test/resources/";
  String[] advancedSourceFileNames = {
      "binarySortTree.wacc",
      "hashTable.wacc",
      "ticTacToe.wacc"
  };

  private FrontEndAnalyser buildFrontEndAnalyser(String sourceFilePath)
      throws IOException {
    CharStream source = CharStreams.fromStream(new FileInputStream(sourceFilePath));
    return new FrontEndAnalyser(source);
  }

  @Test
  public void compilesAdvancedProgramsSuccessfully() throws IOException {
    for (String name : advancedSourceFileNames) {
      String sourceFilePath = resourcesFolderPath + name;
      assertEquals(buildFrontEndAnalyser(sourceFilePath).run(), 0);
    }
  }
}
