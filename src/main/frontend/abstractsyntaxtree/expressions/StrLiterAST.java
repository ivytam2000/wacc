package frontend.abstractsyntaxtree.expressions;

import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;
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
    return null;
  }
}
