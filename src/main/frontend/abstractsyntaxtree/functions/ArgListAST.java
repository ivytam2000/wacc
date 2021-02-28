package frontend.abstractsyntaxtree.functions;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import java.util.Collections;
import java.util.List;

public class ArgListAST extends Node {

  private List<Node> expressions;

  public ArgListAST(List<Node> expressions) {
    this.expressions = expressions;
  }

  public List<Node> getArguments() {
    return expressions;
  }

  @Override
  public void check() {
  }

  @Override
  public List<Instr> toAssembly() {
    return Collections.emptyList();
  }
}
