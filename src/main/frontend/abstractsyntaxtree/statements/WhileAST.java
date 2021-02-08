package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.BoolID;
import frontend.symboltable.TypeID;

public class WhileAST extends Node {
  private final Node expr;
  private final Node stat;

  public WhileAST(Node expr, Node stat) {
    super();
    this.expr = expr;
    this.stat = stat;
  }

  @Override
  public void check() {
    TypeID exprType = expr.getIdentifier().getType();
    if (!(exprType instanceof BoolID)) {
      System.out.println("Condition is not of type bool ");
    }
  }
}
