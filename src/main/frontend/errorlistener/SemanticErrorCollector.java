package frontend.errorlistener;

import java.util.ArrayList;
import java.util.List;

public class SemanticErrorCollector {

  private static final List<String> errors = new ArrayList<>();

  public static void addError(String s) {
    errors.add(s);
  }

  public static void printErrors() {
    System.out.println("Errors detected during compilation! Exit code 200 returned.");
    for (String s : errors) {
      System.out.println("Semantic Error at " + s);
    }
  }

  public static void addIncompatibleType(String expected, String actual, int line, int position) {
    String errorMsg = String.format("line %d:%d -- Expected type %s, Actual type %s", line, position, expected, actual);
    addError(errorMsg);
  }

  public static void addVariableUndefined(String varName, int line, int position) {
    String errorMsg = String.format("line %d:%d -- %s is not defined", line, position, varName);
    addError(errorMsg);
  }

  public static void init() {
    errors.clear();
  }

  public static int getNumberOfSemanticErrors() {
    return errors.size();
  }

}
