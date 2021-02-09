package frontend;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestUtilities {

  static FrontEndAnalyser buildFrontEndAnalyser(String sourceFilePath) throws IOException {
    CharStream source = CharStreams.fromStream(new FileInputStream(sourceFilePath));
    return new FrontEndAnalyser(source);
  }

  private static List<String> getTestNames(String sourcesFolderPath) throws IOException {
    return Files.list(Paths.get(sourcesFolderPath))
        .filter(Files::isRegularFile)
        .map(p -> p.getFileName().toString())
        .filter(f->f.endsWith(".wacc"))
        .collect(Collectors.toList());
  }

  public static void main(String[] args) throws IOException {
    List<String> names = getTestNames("src/test/examples/invalid/syntaxErr"
        + "/array");
    for(String s:names){
      System.out.println(s);

    }
    System.out.println("HELLO");
  }

  // Function that checks that the example compiles with a certain exit code
  static void exitsWith(String folderPath, int exitCode) throws IOException {
    List<String> names = getTestNames(folderPath);
    for (String name : names) {
      String sourceFilePath = folderPath + name;
      OutputStream os = new ByteArrayOutputStream();
      System.setOut(new PrintStream(os));
      FrontEndAnalyser analyser = buildFrontEndAnalyser(sourceFilePath);
      try {
        assertEquals(analyser.run(), exitCode);
      } catch (AssertionError e) {
        fail("Test " + name + " did not exit with exit code " + exitCode);
      }
    }
  }
}
