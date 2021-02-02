import antlr.BasicLexer;
import antlr.BasicParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {

  public static void main(String[] args) throws Exception {
    // create a Char stream that reads from standard input
    CharStream input = CharStreams.fromStream(System.in);

    // create a lexer that reads from the input stream
    BasicLexer lexer = new BasicLexer(input);

    // create a buffer of tokens read from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // create a parser that reads from the tokens buffer
    BasicParser parser = new BasicParser(tokens);

    // begin parsing at rule for program
    ParseTree tree = parser.prog();

    // print a LISP-style parse tree
    System.out.println(tree.toStringTree(parser));
  }
}
