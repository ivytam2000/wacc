import antlr.WaccLexer;
import antlr.WaccParser;
import frontend.FrontEndAnalyser;
import frontend.errorlistener.SyntaxErrorListener;
import java.io.FileInputStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {

  public static void main(String[] args) throws Exception {
    // Create a CharStream that reads from an input file
    CharStream input = CharStreams.fromStream(new FileInputStream(args[0]));

    FrontEndAnalyser frontEndAnalyser = new FrontEndAnalyser(input);
    System.exit(frontEndAnalyser.run());
  }
}
