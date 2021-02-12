package frontend;

import antlr.WaccLexer;
import antlr.WaccParser;
import frontend.abstractsyntaxtree.AST;
import frontend.abstractsyntaxtree.Node;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestUtilities {

  public static FrontEndAnalyser buildFrontEndAnalyser(String sourceFilePath) throws IOException {
    CharStream source = CharStreams.fromStream(new FileInputStream(sourceFilePath));
    return new FrontEndAnalyser(source);
  }

  private static List<String> getTestNames(String sourcesFolderPath) throws IOException {
    return Files.list(Paths.get(sourcesFolderPath))
        .filter(Files::isRegularFile)
        .map(p -> p.getFileName().toString())
        .filter(f -> f.endsWith(".wacc"))
        .collect(Collectors.toList());
  }

  // Function that checks that the example compiles with a certain exit code
  public static void exitsWith(String folderPath, int exitCode) throws IOException {
    List<String> names = getTestNames(folderPath);
    for (String name : names) {
      String sourceFilePath = folderPath + name;
      // Redirects standard output to prevent clogging up the CI pipeline 
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

  // Only call on syntactically programs. Used to test semantics.
  private static final String baseDir = "src/test/examples/custom/";

  public static AST buildAST(String filename) throws IOException {
    CharStream input = CharStreams.fromStream(new FileInputStream(baseDir + filename));

    // Create a lexer that reads from the input stream
    WaccLexer lexer = new WaccLexer(input);
    // Create a buffer of tokens read from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // Create a parser that reads from the tokens buffer
    WaccParser parser = new WaccParser(tokens);
    // Parse tokens with the program rule
    ParseTree tree = parser.program();

    // Semantic checking
    TreeVisitor treeVisitor = new TreeVisitor();
    return (AST) treeVisitor.visit(tree);
  }
}
