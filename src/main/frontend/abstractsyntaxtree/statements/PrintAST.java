package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;

public class PrintAST extends Node {
  private final Node expr;

  public PrintAST(Node expr) {
    this.expr = expr;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {}
}
