package frontend.abstractsyntaxtree;

import frontend.symboltable.SymbolTable;

public class AST extends Node {

  private SymbolTable symtab;

  public AST(SymbolTable topLevelSymTab) {
    super(); //Parent is null
    this.symtab =topLevelSymTab;
  }

  @Override
  public void check() {

  }
}
