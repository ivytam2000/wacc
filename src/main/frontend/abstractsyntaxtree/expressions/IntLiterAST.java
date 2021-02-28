package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

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
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();

    // Move value directly into target register
    instrs.add(new MOV("", Instr.getTargetReg(), val));

    return instrs;
  }
}
