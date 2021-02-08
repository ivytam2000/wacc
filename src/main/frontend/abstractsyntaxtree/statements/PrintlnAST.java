package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;

public class PrintlnAST extends Node {
  private final Node expr;

  public PrintlnAST(Node expr) {
    super(expr.getIdentifier());
    this.expr = expr;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {}
}
