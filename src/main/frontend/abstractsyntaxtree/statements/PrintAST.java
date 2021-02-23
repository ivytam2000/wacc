package frontend.abstractsyntaxtree.statements;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import java.util.List;

public class PrintAST extends Node {

  private final Node expr;

  public PrintAST(Node expr) {
    super(expr.getIdentifier());
    this.expr = expr;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {
  }

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
