package frontend.abstractsyntaxtree.statements;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import java.util.List;

public class BeginStatAST extends Node {

  private final Node stat;

  public BeginStatAST(Node stat) {
    super(stat.getIdentifier());
    this.stat = stat;
  }

  @Override
  public void check() {
  }

  @Override
  public List<Instr> toAssembly() {
    return null;
  }

  public Node getStat() {
    return stat;
  }
}
