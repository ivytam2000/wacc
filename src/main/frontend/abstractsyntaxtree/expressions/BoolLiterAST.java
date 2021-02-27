package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class BoolLiterAST extends Node {

  private final boolean val; //For backend

  public BoolLiterAST(SymbolTable symtab, Boolean val) {
    super(symtab.lookupAll("bool"));
    this.val = val;
  }

  public String getVal() {
    return val ? "1": "0";
  }

  @Override
  public void check() {
  }

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
