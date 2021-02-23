import backend.BackEndGenerator;
import backend.instructions.Instr;
import frontend.FrontEndAnalyser;
import java.io.FileInputStream;

import java.util.List;
import org.antlr.v4.runtime.*;

public class Main {

  public static void main(String[] args) throws Exception {
    // Create a CharStream that reads from an input file
    CharStream input = CharStreams.fromStream(new FileInputStream(args[0]));

    // Initialises lexer, parser and semantic analyser
    FrontEndAnalyser frontEndAnalyser = new FrontEndAnalyser(input);
    int status = frontEndAnalyser.run();
    if (status != 0) {
      System.exit(status);
    }

    // Backend
    BackEndGenerator backEndGenerator = new BackEndGenerator(frontEndAnalyser.getAst());
    List<Instr> instructions = backEndGenerator.generate();

    // printer.print(instructions);
  }
}
