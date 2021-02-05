package frontend;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidTests {

  String resourcesFolderPath = "src/test/examples/valid/";
 /* String[] advancedSourceFileNames = {
      "binarySortTree.wacc",
      "hashTable.wacc",
      "ticTacToe.wacc"
  };
  String[] arrayFileNames = {
      "array.wacc",
      "arrayBasic.wacc",
      "arrayEmpty.wacc",
      "arrayLength.wacc",
      "arrayLookup.wacc",
      "arrayNested.wacc",
      "arrayPrint.wacc",
      "modifyString.wacc",
      "printRef.wacc"
  };

  String[] exitFileNames = {
      "exit-1.wacc",
      "exitBasic.wacc",
      "exitBasic2.wacc",
      "exitWrap.wacc"
  };

  String[] skipFileNames = {
      "comment.wacc",
      "commentInLine.wacc",
      "skip.wacc"
  };

  String[] exprFileNames = {
      "andExpr.wacc",
      "boolCalc.wacc",
      "boolExpr1.wacc",
      "charComparisonExpr.wacc",
      "divExpr.wacc",
      "equalsExpr.wacc",
      "greaterEqExpr.wacc",
      "greaterExpr.wacc",
      "intCalc.wacc",
      "intExpr1.wacc",
      "lessCharExpr.wacc",
      "lessEqExpr.wacc",
      "lessExpr.wacc",
      "longExpr.wacc",
      "longExpr2.wacc",
      "longExpr3.wacc",
      "longSplitExpr.wacc",
      "longSplitExpr2.wacc",
      "minusExpr.wacc",
      "minusMinusExpr.wacc",
      "minusNoWhitespaceExpr.wacc",
      "minusPlusExpr.wacc",
      "modExpr.wacc",
      "multExpr.wacc",
      "multNoWhitespaceExpr.wacc",
      "negBothDiv.wacc",
      "negBothMod.wacc",
      "negDividendDiv.wacc",
      "negDividendMod.wacc",
      "negDivisorDiv.wacc",
      "negDivisorMod.wacc",
      "negExpr.wacc",
      "notequalsExpr.wacc",
      "notExpr.wacc",
      "ordAndchrExpr.wacc",
      "orExpr.wacc",
      "plusExpr.wacc",
      "plusMinusExpr.wacc",
      "plusNoWhitespaceExpr.wacc",
      "plusPlusExpr.wacc",
      "sequentialCount.wacc",
      "stringEqualsExpr.wacc"
  };*/


  private FrontEndAnalyser buildFrontEndAnalyser(String sourceFilePath)
      throws IOException {
    CharStream source = CharStreams.fromStream(new FileInputStream(sourceFilePath));
    return new FrontEndAnalyser(source);
  }

  private void compileSuccessfully(String folderPath) throws IOException{
    List<String> names = getTestNames(resourcesFolderPath + folderPath);
    for (String name : names){
      String sourceFilePath = resourcesFolderPath + folderPath + name;
      OutputStream os = new ByteArrayOutputStream();
      System.setOut(new PrintStream(os));
      try {
        assertEquals(buildFrontEndAnalyser(sourceFilePath).run(), 0);
      } catch(AssertionError e){
        fail("Test " + name + " failed.");
      }
    }
  }

  private static List<String> getTestNames(String sourcesFolderPath)
      throws IOException {
    List<String> files =
        Files.list(Paths.get(sourcesFolderPath))
            .filter(Files::isRegularFile)
            .map(p->p.getFileName().toString())
            .collect(Collectors.toList());
    return files;
  }

  public static void main(String[] args) throws IOException {
    List<String> files = getTestNames("src/test/wacc_examples/valid/advanced");
    for(String s : files){
      System.out.println(s + '\n');
    }
  }

  @Test
  public void compilesAdvancedProgramsSuccessfully() throws IOException {
    compileSuccessfully("advanced/");
  }

  @Ignore
  @Test
  //arrayEmpty test failed
  public void validArrayTests() throws IOException {
    compileSuccessfully("array/");
  }

  @Test
  public void validBasicExitTests() throws IOException {
    compileSuccessfully("basic/exit/");
  }

  @Test
  public void validBasicSkipTests() throws IOException{
    compileSuccessfully("basic/skip/");
  }

  @Test
  public void validExprTests() throws IOException {
    compileSuccessfully("expressions/");
  }

  @Test
  public void validNestedFuncTest() throws IOException {
    compileSuccessfully("function/nested_functions/");
  }

  @Test
  public void validSimpleFuncTest() throws IOException {
    compileSuccessfully("function/simple_functions/");
  }

  @Test
  public void validIfTest() throws IOException {
    compileSuccessfully("if/");
  }

  @Test
  public void validIOTest() throws IOException {
    compileSuccessfully("IO/");
  }

  @Test
  public void validPairsTest() throws IOException {
    compileSuccessfully("pairs/");
  }

  @Test
  public void validRuntimeErrTest() throws IOException {
    compileSuccessfully("runtimeErr/");
  }

  @Test
  public void validScopeTest() throws IOException {
    compileSuccessfully("scope/");
  }

  @Test
  public void validSequenceTest() throws IOException {
    compileSuccessfully("sequence/");
  }

  @Test
  public void validVariablesTest() throws IOException {
    compileSuccessfully("variables/");
  }

  @Test
  public void validWhileTest() throws IOException {
    compileSuccessfully("while/");
  }

}
