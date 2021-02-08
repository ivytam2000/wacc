import frontend.FrontEndAnalyser;
import java.io.FileInputStream;

import org.antlr.v4.runtime.*;

public class Main {

  public static void main(String[] args) throws Exception {
    // Create a CharStream that reads from an input file
    CharStream input = CharStreams.fromStream(new FileInputStream(args[0]));
    // FOR DEBUGGING:
    //CharStream input = CharStreams.fromStream(System.in);
    FrontEndAnalyser frontEndAnalyser = new FrontEndAnalyser(input);
    System.exit(frontEndAnalyser.run());
  }
}
