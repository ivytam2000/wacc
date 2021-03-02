package backend;

import static frontend.TestUtilities.getTestNames;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TestUtilities {

  /**
   * Gets text file path of .wacc file
   */
  static String getTextFilePath(String folderPath, String name) {
    name = name.replace(".wacc", ".txt");
    folderPath = folderPath.replace("valid", "expectedOutputs");
    return folderPath + name;
  }

  /**
   * Checks that the example compiles with a certain exit code.
   */
  public static void executablesFromOurCompilerMatchesReferenceCompiler(
      String folderPath) throws IOException {
    List<String> names = getTestNames(folderPath);
    for (String name : names) {
      String sourceFilePath = folderPath + name;
      String textFilePath = getTextFilePath(folderPath, name);
      try {
        assertTrue(
            executableFromOurCompilerMatchesReferenceCompiler(sourceFilePath, textFilePath));
      } catch (AssertionError e) {
        fail("Test " + name + " output did not match with reference compiler");
      } catch (IOException e) {
        fail("Process Builder failed to start!");
      }
    }
  }

  /**
   * Returns the standard output from a process containing terminal commands.
   */
  static String getOutputFromProcess(ProcessBuilder builder)
      throws IOException {
    // Start process
    Process process = builder.start();
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()));
    PrintStream out = new PrintStream(
        new BufferedOutputStream(process.getOutputStream()));
    out.close();

    // Get output from reference compiler
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line);
      stringBuilder.append(System.getProperty("line.separator"));
    }

    return stringBuilder.toString();
  }

  /**
   * Assembles a .wacc file and returns the file path to the resulting .s file.
   */
  private static String assembleFileWithOurCompiler(String filePath)
      throws IOException {
    // Bash command to use our compiler
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("./compile", filePath);

    getOutputFromProcess(builder);

    return filePath.replace(".wacc", ".s");
  }

  /**
   * Compiles and emulates a .wacc file, returns the filtered standard output
   * stream from the resulting executable compiled by our compiler.
   */
  private static List<String> getOurCompilerStdOut(String filePath)
      throws IOException {
    // Get assembly file path
    String assemblyFilePath = assembleFileWithOurCompiler(filePath);

    // Get standard output stream from reference emulator
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("./refEmulate", assemblyFilePath);
    String output = getOutputFromProcess(builder);
    String[] splitOutput = output.split("\n");

    List<String> actualStdOuts = new ArrayList<>();
    boolean nextLineIsOutput = false;

    for (String line : splitOutput) {
      // Checks if the next line is unwanted output
      if (nextLineIsOutput) {
        if (line.contains("---")) {
          return actualStdOuts;
        } else {
          if (!line.isEmpty()) {
            actualStdOuts.add(line);
          }
        }
      }

      // Checks if the next line is wanted output from the compiled program
      if (line.contains("Emulation Output")) {
        nextLineIsOutput = true;
      }
    }
    return actualStdOuts;
  }

  /**
   * Compiles and emulates a .wacc file, returns the filtered standard output
   * stream from the resulting executable compiled by the reference compiler.
   */
  private static List<String> getReferenceCompilerStdOut(String filePath)
      throws IOException {
    // Get standard output stream from reference compiler and emulator
    BufferedReader br = new BufferedReader(new FileReader(filePath));
    List<String> expectedValues = new ArrayList<>();

    String line;
    while ((line = br.readLine()) != null) {
      expectedValues.add(line);
    }

    return expectedValues;
  }

  /**
   * Separately compiles a .wacc file through our compiler and the reference
   * compiler respectively, emulates two resulting executables through the
   * reference emulator, checks if the standard output from the executables are
   * equal.
   */
  private static boolean executableFromOurCompilerMatchesReferenceCompiler(
      String filePath, String txtFilePath) throws IOException {
    List<String> actualOutput = getOurCompilerStdOut(filePath);
    List<String> expectedOutput = getReferenceCompilerStdOut(
        txtFilePath);
    return actualOutput.equals(expectedOutput);
  }
}
