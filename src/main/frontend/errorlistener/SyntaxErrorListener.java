package frontend.errorlistener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxErrorListener extends BaseErrorListener {

  private final List<String> errorMessages = new ArrayList<>();
  private boolean intOverflowDetected = false;

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
      int line, int charPositionInLine, String msg, RecognitionException e) {
    errorMessages.add(line + ":" + charPositionInLine + " -- " + msg);
  }

  public void intError(int line, boolean over) {
    this.intOverflowDetected = true;
    String status = "";
    if (over) {
      status = "overflow";
    } else {
      status = "underflow";
    }
    errorMessages.add(line + " -- Integer " + status + " detected");
  }

  public boolean hasIntError() {
    return this.intOverflowDetected;
  }

  public void printErrors() {
    System.out
        .println("Errors during compilation! Exit code 100 returned.");
    for (String s : errorMessages) {
      System.out.println("Syntactic error at line " + s);
    }
    System.out.println(errorMessages.size()
        + " syntax error(s) detected, no further compilation attempted");
  }
}
