package frontend.abstractsyntaxtree.expressions;

import backend.BackEndGenerator;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public class CharLiterAST extends Node {

  private final String val; //For backend

  public CharLiterAST(SymbolTable symtab, String val) {
    super(symtab.lookupAll("char"));
    this.val = val;
  }

  public String getVal() {
    return "#" + "'" + val + "'" ;
  }

  @Override
  public void check() {}

  @Override
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();

    // Move character into target register
    instrs.add(new MOV("", Instr.getTargetReg(), getVal()));

    return instrs;
  }
}
