package frontend.abstractsyntaxtree.statements;

import backend.BackEndGenerator;
import backend.Utils;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;

import java.util.ArrayList;
import java.util.List;

import static backend.Utils.getPrintBranch;

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
    // assumes that the expr.toAssembly() will load or mov the expr's value into
    // R4.
    List<Instr> instrs = new ArrayList<>(expr.toAssembly());
    MOV movInstr = new MOV("", Instr.R0, Instr.R4);
    instrs.add(movInstr);
    instrs.add(getPrintBranch(expr.getIdentifier().getType()));
    BackEndGenerator.addToPreDefFunc("p_print_ln");
    instrs.add(new BRANCH(true, "", "p_print_ln"));
    return instrs;
  }
}
