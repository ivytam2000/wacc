package frontend.abstractsyntaxtree.statements;

import backend.BackEndGenerator;
import backend.instructions.AddrMode;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;

import java.util.ArrayList;
import java.util.List;

import static backend.Utils.getPrintBranch;
import static backend.instructions.Instr.addToCurLabel;

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
  public void toAssembly(){
    // assumes that the expr.toAssembly() will load or mov the expr's value into
    // R4.
    expr.toAssembly();
    List<Instr> instrs = new ArrayList<>();
    MOV movInstr = new MOV("", Instr.R0, AddrMode.buildReg(Instr.R4));
    instrs.add(movInstr);
    instrs.add(getPrintBranch(expr.getIdentifier().getType()));
    BackEndGenerator.addToPreDefFuncs("p_print_ln");
    instrs.add(new BRANCH(true, "", "p_print_ln"));
    addToCurLabel(instrs);
  }
}
