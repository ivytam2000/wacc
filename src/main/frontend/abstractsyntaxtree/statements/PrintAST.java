package frontend.abstractsyntaxtree.statements;

import backend.Utils;
import backend.instructions.AddrMode;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.*;

import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class PrintAST extends Node {

  private final Node expr;
  private final SymbolTable symtab;

  public PrintAST(Node expr, SymbolTable symtab) {
    super(expr.getIdentifier());
    this.expr = expr;
    this.symtab = symtab;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {}

  @Override
  public void toAssembly() {
    // assumes that the expr.toAssembly() will load or mov the expr's value into
    // R4.
    expr.toAssembly();
    List<Instr> instrs = new ArrayList<>();
    MOV movInstr = new MOV("", Instr.R0, AddrMode.buildReg(Instr.R4));
    instrs.add(movInstr);
    BRANCH brInstr = Utils.getPrintBranch(expr.getIdentifier().getType());
    instrs.add(brInstr);
    addToCurLabel(instrs);
  }


}
