import antlr.BasicLexer;
import antlr.BasicParser;
import frontend.errorlistener.SyntaxErrorListener;
import java.io.FileInputStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {

  public static void main(String[] args) throws Exception {
    // create a Char stream that reads from standard input
    CharStream input = CharStreams.fromStream(new FileInputStream(args[0]));

    // create a lexer that reads from the input stream
    BasicLexer lexer = new BasicLexer(input);

    // create a buffer of tokens read from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // create a parser that reads from the tokens buffer
    BasicParser parser = new BasicParser(tokens);
    parser.removeErrorListeners(); //Remove standard error listener
    SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
    parser.addErrorListener(syntaxErrorListener);

    System.out.println("--- Compiling... ---");

    // begin parsing at rule for program
    ParseTree tree = parser.program();

    if (parser.getNumberOfSyntaxErrors() > 0) {
      syntaxErrorListener.printErrors();
      System.exit(100);
    }

    // print a LISP-style parse tree
    System.out.println("--- Parsing finished... ---");
    System.out.println(tree.toStringTree(parser));
  }
}
