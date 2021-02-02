package frontend.errorlistener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxErrorListener extends BaseErrorListener {

  private final List<String> errorMessages = new ArrayList<>();

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
      int line, int charPositionInLine, String msg, RecognitionException e) {
//    List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
//    Collections.reverse(stack);
//    System.err.println("rule stack: " + stack);
    errorMessages.add(line + ":" + charPositionInLine + " -- " + msg);
  }

  public void printErrors() {
    System.out
        .println("Errors detected during compilation! Exit code 100 returned.");
    for (String s : errorMessages) {
      System.out.println("Syntactic error at line " + s);
    }
    System.out.println(errorMessages.size()
        + " syntax error(s) detected, no further compilation attempted");
  }
}
