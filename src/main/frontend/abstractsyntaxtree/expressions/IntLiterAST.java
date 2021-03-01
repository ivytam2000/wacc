package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import backend.instructions.LDR;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class IntLiterAST extends Node {

  private final String val; //For backend

  public IntLiterAST(SymbolTable symtab, boolean positive, String val) {
    super(symtab.lookupAll("int"));
    this.val = (positive ? "" : "-") + val;
  }

  public String getVal() {
    return val;
  }

  @Override
  public void check() {

  }

  @Override
  public void toAssembly() {
    List<Instr> instrs = new ArrayList<>();

    // Load value directly into target register
    instrs.add(new LDR(Instr.getTargetReg(), val));

    addToCurLabel(instrs);
  }
}
