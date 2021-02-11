package frontend;

import antlr.WaccLexer;
import antlr.WaccParser;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.errorlistener.SyntaxErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class FrontEndAnalyser {

  private final WaccParser parser;
  private final SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();

  public FrontEndAnalyser(CharStream stream) {
    // Create a lexer that reads from the input stream
    WaccLexer lexer = new WaccLexer(stream);
    // Remove standard error listeners from lexer
    lexer.removeErrorListeners();
    lexer.addErrorListener(syntaxErrorListener);

    // Create a buffer of tokens read from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // Create a parser that reads from the tokens buffer
    parser = new WaccParser(tokens, syntaxErrorListener);
    // Remove standard error listener from parser
    parser.removeErrorListeners();
    parser.addErrorListener(syntaxErrorListener);
  }

  public int run() {
    System.out.println("--- Compiling... ---");

    // Begin parsing at rule for program
    ParseTree tree = parser.program();

    /* Second and third case needed because we treat
    int overflow and return error separately */
    if (parser.getNumberOfSyntaxErrors() > 0 ||
        syntaxErrorListener.hasIntError() ||
        syntaxErrorListener.hasReturnError()) {
      syntaxErrorListener.printErrors();
      return 100;
    }

    SemanticErrorCollector.init();
    TreeVisitor treeVisitor = new TreeVisitor();
    Node ast = treeVisitor.visit(tree);

    if (SemanticErrorCollector.getNumberOfSemanticErrors() > 0) {
      SemanticErrorCollector.printErrors();
      return 200;
    }

    // Print a LISP-style parse tree
    System.out.println("--- Parsing finished... ---");

    //For debugging
    //System.out.println(tree.toStringTree(parser));

    return 0;
  }
}
