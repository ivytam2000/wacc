package frontend;

import antlr.WaccLexer;
import antlr.WaccParser;
import frontend.errorlistener.SyntaxErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class FrontEndAnalyser {

  private final CharStream stream;
  private final WaccLexer lexer;
  private final WaccParser parser;
  private final SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();

  public FrontEndAnalyser(CharStream stream) {
    this.stream = stream;

    // Create a lexer that reads from the input stream
    lexer = new WaccLexer(stream);

    // Create a buffer of tokens read from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // Create a parser that reads from the tokens buffer
    parser = new WaccParser(tokens);
    parser.removeErrorListeners(); // Remove standard error listener
    parser.addErrorListener(syntaxErrorListener);
  }

  public WaccParser getParser() {
    return parser;
  }

  public int run() {
    System.out.println("--- Compiling... ---");

    // Begin parsing at rule for program
    ParseTree tree = parser.program();

    if (parser.getNumberOfSyntaxErrors() > 0) {
      syntaxErrorListener.printErrors();
      return 100;
    }

    TreeVisitor treeVisitor = new TreeVisitor();
    treeVisitor.visit(tree);

    // Print a LISP-style parse tree
    System.out.println("--- Parsing finished... ---");
    System.out.println(tree.toStringTree(parser));

    return 0;
  }
}
