package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;

public class BeginStatAST extends Node {

  private final Node stat;

  public BeginStatAST(Node stat) {
    super(stat.getIdentifier());
    this.stat = stat;
  }

  @Override
  public void check() {}
}
