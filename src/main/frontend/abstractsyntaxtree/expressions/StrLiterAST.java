package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;

public class StrLiterAST extends Node {

  private final String val; //For backend

  public StrLiterAST(SymbolTable symtab, String val) {
    super(symtab.lookupAll("string"));
    assert (super.identifier != null);
    this.val = val;
  }

  @Override
  public void check() {}
}
