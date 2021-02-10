package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.TypeID;

public class IfAST extends Node {

  private final Node expr;
  private final Node thenStat;
  private final Node elseStat;

  public IfAST(Node expr, Node thenStat, Node elseStat) {
    // set identifier to be same as expressions but not sure if its correct?
    super(expr.getIdentifier());
    this.expr = expr;
    this.thenStat = thenStat;
    this.elseStat = elseStat;
  }

  public Node getThenStat() {
    return thenStat;
  }

  public Node getElseStat() {
    return elseStat;
  }

  @Override
  public void check() {
    TypeID exprType = expr.getIdentifier().getType();
    if (!(exprType instanceof BoolID)) {
      SemanticErrorCollector.addError(
          "Condition type is not of expected "
              + "type: bool, actual "
              + "type: "
              + exprType.getTypeName());
    }
  }
}
