package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.BoolID;
import frontend.symboltable.TypeID;

public class IfAST extends Node {

  private final Node expr;
  private final Node thenStat;
  private final Node elseStat;

  public IfAST(Node expr, Node thenStat, Node elseStat) {
    this.expr = expr;
    this.thenStat = thenStat;
    this.elseStat = elseStat;
  }

  @Override
  public void check() {
    TypeID exprType = expr.getIdentifier().getType();
    if (!(exprType instanceof BoolID)) {
      System.out.println("Condition is not of type bool ");
    }
  }
}
