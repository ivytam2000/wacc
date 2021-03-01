package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class BoolLiterAST extends Node {

  private final boolean val; //For backend

  public BoolLiterAST(SymbolTable symtab, Boolean val) {
    super(symtab.lookupAll("bool"));
    this.val = val;
  }

  public String getVal() {
    return val ? "#1": "#0";
  }

  @Override
  public void check() {
  }

  @Override
  public void toAssembly() {
    List<Instr> instrs = new ArrayList<>();

    // Move character into target register
    instrs.add(new MOV("", Instr.getTargetReg(), getVal()));

    addToCurLabel(instrs);
  }
}
