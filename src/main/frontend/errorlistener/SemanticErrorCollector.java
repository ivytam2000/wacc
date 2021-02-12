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

  public static void addIncompatibleType(String expected, String actual,
      String token, int line, int position) {
    String errorMsg = String.format("line %d:%d -- Incompatible Types at %s, "
        + "Expected Type: %s, "
        + "but Actual Type: %s", line, position, token, expected, actual);
    addError(errorMsg);
  }

  public static void addUnknownType(String typeName, int line, int position) {
    String errorMsg = String
        .format("line %d:%d -- Type %s is not defined", line, position, typeName);
    addError(errorMsg);
  }

  public static void addVariableUndefined(String varName, int line, int position) {
    String errorMsg = String
        .format("line %d:%d -- Variable %s is not defined", line, position, varName);
    addError(errorMsg);
  }

  public static void addSymbolAlreadyDefined(String symbol, int line, int position) {
    String errorMsg = String
        .format("line %d:%d -- \"%s\" is already defined in this scope", line, position, symbol);
    addError(errorMsg);
  }

  public static void addFunctionUndefined(String funcName, int line, int position) {
    String errorMsg = String
        .format("line %d:%d -- Function %s is not defined", line, position, funcName);
    addError(errorMsg);
  }

  public static void addTypeMismatch(int line, int position, String op) {
    String errorMsg = String
        .format("line %d:%d -- Type mismatch at %s", line, position, op);
    addError(errorMsg);
  }

  public static void init() {
    errors.clear();
  }

  public static int getNumberOfSemanticErrors() {
    return errors.size();
  }

}
