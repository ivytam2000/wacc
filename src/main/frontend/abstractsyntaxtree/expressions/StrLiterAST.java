package frontend.abstractsyntaxtree.expressions;

import backend.BackEndGenerator;
import backend.instructions.Instr;
import backend.instructions.LDR;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public class StrLiterAST extends Node {

  private final String val; //For backend

  public StrLiterAST(SymbolTable symtab, String val) {
    super(symtab.lookupAll("string"));
    assert (super.identifier != null);
    this.val = val;
  }

  @Override
  public void check() {}

  public String getVal() {
    return val;
  }

  @Override
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();

    // Add string to data segment and load message directly into target register
    int index = BackEndGenerator.addToDataSegment(val);
    instrs.add(new LDR(Instr.getTargetReg(), "msg_" + index));

    return instrs;
  }
}
