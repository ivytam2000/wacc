package frontend.abstractsyntaxtree.statements;

import backend.Utils;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.*;

import java.util.ArrayList;
import java.util.List;

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
  public List<Instr> toAssembly() {
    // assumes that the expr.toAssembly() will load or mov the expr's value into
    // R4.
    List<Instr> instrs = new ArrayList<>(expr.toAssembly());
    MOV movInstr = new MOV("", Instr.R0, Instr.R4);
    instrs.add(movInstr);
    BRANCH brInstr = Utils.getPrintBranch(expr.getIdentifier().getType());
    instrs.add(brInstr);
    return instrs;
  }


}
