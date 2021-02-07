package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;

public class BoolLiterAST extends Node {

  private final boolean val;

  public BoolLiterAST(SymbolTable symtab, Boolean val) {
    super(symtab.lookupAll("bool"));
    assert (super.identifier != null);
    this.val = val;
  }

  @Override
  public void check() {
  }
}
