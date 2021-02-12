package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;

public class BoolLiterAST extends Node {

  private final boolean val; //For backend

  public BoolLiterAST(SymbolTable symtab, Boolean val) {
    super(symtab.lookupAll("bool"));
    this.val = val;
  }

  @Override
  public void check() {
  }
}
