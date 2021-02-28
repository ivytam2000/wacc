import backend.BackEndGenerator;
import backend.instructions.Instr;
import frontend.FrontEndAnalyser;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.*;

public class Main {

  public static void main(String[] args) throws Exception {

    String srcFilePath = args[0];

    // Create a CharStream that reads from an input file
    CharStream input = CharStreams.fromStream(new FileInputStream(srcFilePath));

    // Initialises and runs the lexer, parser and semantic analyser
    FrontEndAnalyser frontEndAnalyser = new FrontEndAnalyser(input);
    int status = frontEndAnalyser.run();
    if (status != 0) {
      System.exit(status);
    }

    // Initialises and runs the code generator
    BackEndGenerator backEndGenerator = new BackEndGenerator(
        frontEndAnalyser.getAst());
    String assFilePath = srcFilePath.replace(".wacc", ".s");
    File assFile = new File(assFilePath);
    assFile.createNewFile();

    FileWriter assFileWriter = new FileWriter(assFilePath, false);
    String output = backEndGenerator.run();
    assFileWriter.write(output);
    assFileWriter.close();
  }
}
