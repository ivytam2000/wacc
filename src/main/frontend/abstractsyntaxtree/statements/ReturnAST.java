package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;

public class ReturnAST extends Node {
  private final Node expr;

  public ReturnAST(Node expr) {
    this.expr = expr;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {}
}
