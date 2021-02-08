package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;

import java.util.List;

public class SequenceAST extends Node {

  private final List<Node> statements;

  public SequenceAST(List<Node> statements) {
    // TODO: what identifier??
    this.statements = statements;
  }

  @Override
  public void check() {}
}
