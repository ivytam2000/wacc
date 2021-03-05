package frontend.abstractsyntaxtree.expressions;

import backend.instructions.AddrMode;
import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.FALSE_VAL;
import static backend.instructions.Instr.TRUE_VAL;
import static backend.instructions.Instr.addToCurLabel;

public class BoolLiterAST extends Node {

  private final boolean val;

  public BoolLiterAST(SymbolTable symtab, Boolean val) {
    super(symtab.lookupAll("bool"));
    this.val = val;
  }

  @Override
  public void check() {
  }

  @Override
  public void toAssembly() {
    List<Instr> instrs = new ArrayList<>();

    // Move character into target register
    AddrMode val = this.val ? AddrMode.buildImm(TRUE_VAL) : AddrMode.buildImm(FALSE_VAL);
    instrs.add(new MOV("", Instr.getTargetReg(), val));

    addToCurLabel(instrs);
  }
}
