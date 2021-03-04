package frontend.abstractsyntaxtree.expressions;

import backend.instructions.AddrMode;
import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class CharLiterAST extends Node {

  private final String val;

  public CharLiterAST(SymbolTable symtab, String val) {
    super(symtab.lookupAll("char"));
    this.val = val;
  }

  public AddrMode getVal() {
    return (val.equals("\\0") ? AddrMode.buildImm(0)
        : AddrMode.buildImm("'" + val + "'"));
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
