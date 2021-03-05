package frontend.abstractsyntaxtree.statements;

import backend.Utils;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;


import java.util.ArrayList;
import java.util.List;

import static backend.instructions.AddrMode.buildReg;
import static backend.instructions.Condition.NO_CON;
import static backend.instructions.Instr.addToCurLabel;

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
  public void check() {}

  @Override
  public void toAssembly() {
    // Evaluate the expression
    expr.toAssembly();
    List<Instr> instrs = new ArrayList<>();
    MOV movInstr = new MOV(NO_CON, Instr.R0, buildReg(Instr.R4));
    instrs.add(movInstr);
    // Branch to print label according to its type
    BRANCH brInstr = Utils.getPrintBranch(expr.getIdentifier().getType());
    instrs.add(brInstr);
    addToCurLabel(instrs);
  }


}
