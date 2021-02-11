package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;

import java.util.List;

public class SequenceAST extends Node {

  private final List<Node> statements;

  public SequenceAST(List<Node> statements) {
    this.statements = statements;
  }

  @Override
  public void check() {}

  //FOR DEBUGGING
  public List<Node> getStatements() {
    return statements;
  }
}
