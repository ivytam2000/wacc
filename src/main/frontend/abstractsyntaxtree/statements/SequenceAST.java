package frontend.abstractsyntaxtree.statements;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;

import java.util.ArrayList;
import java.util.List;

public class SequenceAST extends Node {

  private final List<Node> statements;

  public SequenceAST(List<Node> statements) {
    this.statements = statements;
  }

  @Override
  public void check() {
  }

  @Override
  public void toAssembly() {
    for(Node stat : statements){
      stat.toAssembly();
    }
  }

  public List<Node> getStatements() {
    return statements;
  }
}
