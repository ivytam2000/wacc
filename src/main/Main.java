import antlr.WaccLexer;
import antlr.WaccParser;
import frontend.errorlistener.SyntaxErrorListener;
import java.io.FileInputStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {

  public static void main(String[] args) throws Exception {
    // Create a CharStream that reads from standard input
    CharStream input = CharStreams.fromStream(new FileInputStream(args[0]));

    // Create a lexer that reads from the input stream
    WaccLexer lexer = new WaccLexer(input);

    // Create a buffer of tokens read from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // Create a parser that reads from the tokens buffer
    WaccParser parser = new WaccParser(tokens);
    parser.removeErrorListeners(); // Remove standard error listener
    SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
    parser.addErrorListener(syntaxErrorListener);

    System.out.println("--- Compiling... ---");

    // Begin parsing at rule for program
    ParseTree tree = parser.program();

    if (parser.getNumberOfSyntaxErrors() > 0) {
      syntaxErrorListener.printErrors();
      System.exit(100);
    }

    // Print a LISP-style parse tree
    System.out.println("--- Parsing finished... ---");
    System.out.println(tree.toStringTree(parser));
  }
}
