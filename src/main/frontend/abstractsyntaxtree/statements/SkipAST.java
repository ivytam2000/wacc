package frontend.abstractsyntaxtree.statements;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import java.util.List;

public class SkipAST extends Node {

  public SkipAST() {
  }

  // No need to check as it is a terminal node
  @Override
  public void check() {
  }

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
