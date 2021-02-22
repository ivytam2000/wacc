package backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import frontend.FrontEndAnalyser;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

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
      // TODO: BackEndAnalyser analyser = buildBackEndAnalyser(sourceFilePath);
      FrontEndAnalyser analyser = buildFrontEndAnalyser(sourceFilePath);
      try {
        assertEquals(analyser.run(), exitCode);
      } catch (AssertionError e) {
        fail("Test " + name + " did not exit with exit code " + exitCode);
      }

      try {
        assertTrue(checkFileOutputsMatchRefCompiler(sourceFilePath));
      } catch (AssertionError e) {
        fail("Test " + name + " output did not match with reference compiler");
      } catch (IOException e) {
        fail("Process Builder failed to start!");
      }
    }
  }

  private static String getOutputFromProcess(ProcessBuilder builder) throws IOException {
    // start process
    Process process = builder.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    PrintStream out = new PrintStream(new BufferedOutputStream(process.getOutputStream()));
    out.close();

    // get output from reference compiler
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line);
      stringBuilder.append(System.getProperty("line.separator"));
    }

    return stringBuilder.toString();
  }


  private static String getAssemblyFilepath(String filepath) throws IOException {
    // make process to run terminal commands
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("./compile", filepath);

    getOutputFromProcess(builder);

    return filepath.replace(".wacc", ".s");
  }


  private static List<String> getOurCompilerOutputValue(String filepath) throws IOException {
    // get assembly file path
    String assemblyFilePath = getAssemblyFilepath(filepath);

    // get reference emulator result
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("./refEmulate", assemblyFilePath);
    String output = getOutputFromProcess(builder);
    String[] splitOutput = output.split("\n");

    List<String> actualStdOuts = new ArrayList<>();
    boolean nextLineIsOutput = false;

    for (String line: splitOutput) {
      // checks if the next line is an Std output
      if (nextLineIsOutput) {
        if (line.contains("-------")) {
          return actualStdOuts;
        } else {
          actualStdOuts.add(line);
        }
      }

      // checks if its the emulation output
      if (line.contains("Emulation Output")) {
        nextLineIsOutput = true;
      }
    }
    return actualStdOuts;
  }


  private static String getReferenceCompilerOutput(String filepath)
      throws IOException {
    // get reference compiler result
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("./refCompile", "-x", filepath);
    return getOutputFromProcess(builder);
  }


  private static List<String> getReferenceCompilerExpectedValue(String filepath) throws IOException {
    String output = getReferenceCompilerOutput(filepath);

    String[] separatedOutputByLine = output.split("\n");
    boolean nextLineIsOutput = false;
    List<String> expectedStdOuts = new ArrayList<>();

    for (String line: separatedOutputByLine) {
      // next few lines might be expectedStdOuts
      if (nextLineIsOutput) {

        if (!line.isEmpty()) {
          if (line.charAt(0) == '=') {
            // end of expectedStdOuts
            return expectedStdOuts;
          } else {
            // continue adding StdOuts
            expectedStdOuts.add(line);
          }
        }
      }

      // toggle for nextLineIsOutput to see if the next few lines are StdOuts
      if (!line.isEmpty()) {
        if (line.charAt(0) == '=') {
          nextLineIsOutput = !nextLineIsOutput;
        }
      }
    }
    return expectedStdOuts;
  }


  public static boolean checkFileOutputsMatchRefCompiler(String testFilePath) throws IOException {
    // checks if the output from our compiler is the same as the reference compiler's output
    List<String> actualOutput = getOurCompilerOutputValue(testFilePath);
    List<String> expectedOutput = getReferenceCompilerExpectedValue(testFilePath);
    return actualOutput.equals(expectedOutput);
  }


  public static void main(String[] args) throws IOException {
    System.out.println(getOurCompilerOutputValue("hello.wacc"));
  }

}
