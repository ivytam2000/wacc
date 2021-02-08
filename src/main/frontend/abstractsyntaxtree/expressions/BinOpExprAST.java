package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.CharID;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;

public class BinOpExprAST extends Node {

  public static final int BOOL = 1;
  public static final int INT_CHAR = 2;
  public static final int ALL_TYPES = 3;

  private final String operation;
  private final int expectedExprTypes;
  private final Node eL;
  private final Node eR;

  public BinOpExprAST(SymbolTable symtab, int expectedExprTypes,
      String operation, Node eL, Node eR) {
    super(symtab.lookupAll("bool")); //BinOpExpr always has bool return type
    this.expectedExprTypes = expectedExprTypes;
    this.operation = operation;
    this.eL = eL;
    this.eR = eR;
  }

  @Override
  public void check() {
    boolean error = false;
    if (expectedExprTypes == BOOL) {
      error = !(eL.getIdentifier().getType() instanceof BoolID &&
          eR.getIdentifier().getType() instanceof BoolID);
    } else if (expectedExprTypes == INT_CHAR) {
      boolean isInt = eL.getIdentifier().getType() instanceof IntID &&
          eR.getIdentifier().getType() instanceof IntID;
      boolean isChar = eL.getIdentifier().getType() instanceof CharID &&
          eR.getIdentifier().getType() instanceof CharID;
      error = !(isInt || isChar);
    } else if (expectedExprTypes == ALL_TYPES) {
      //Compare type names to support nested pairs and arrays
      error = !(eL.getIdentifier().getType().getTypeName().equals
          (eR.getIdentifier().getType().getTypeName()));
    }
    if (error) {
      //TODO: Exit
      SemanticErrorCollector.addError("Binary Operator : Incompatible types.");
    }
  }
}
