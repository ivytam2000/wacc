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
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    for(Node stat : statements){
      instrs.addAll(stat.toAssembly());
    }
    return instrs;
  }

  public List<Node> getStatements() {
    return statements;
  }
}
