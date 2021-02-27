package frontend.abstractsyntaxtree.statements;

import backend.instructions.BRANCH;
import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;

import java.util.ArrayList;
import java.util.List;

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
  public void check() {
  }

  @Override
  public List<Instr> toAssembly(){
    List<Instr> instrs = new ArrayList<>();
    // Load label of string to register 4
    // TODO: how to get label?
    // move register 4 to r0
    BRANCH brInstr = new BRANCH(true, "", "p_print_ln");
    instrs.add(brInstr);
    return instrs;
  }
}
