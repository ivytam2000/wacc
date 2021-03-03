package frontend.abstractsyntaxtree.expressions;

import backend.BackEndGenerator;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class CharLiterAST extends Node {

  private final String val; //For backend

  public CharLiterAST(SymbolTable symtab, String val) {
    super(symtab.lookupAll("char"));
    this.val = val;
  }

  public String getVal() {
    return "#" + (val.equals("\\0") ? 0 : "'" + val + "'") ;
  }

  @Override
  public void check() {}

  @Override
  public void toAssembly() {
    List<Instr> instrs = new ArrayList<>();

    // Move character into target register
    instrs.add(new MOV("", Instr.getTargetReg(), getVal()));

    addToCurLabel(instrs);
  }
}
