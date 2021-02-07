package frontend.abstractsyntaxtree;

import frontend.symboltable.SymbolTable;

public class AST extends Node {

  private SymbolTable symtab;

  public AST(SymbolTable topLevelSymTab) {
    super();
    this.symtab =topLevelSymTab;
  }

  @Override
  public void check() {

  }
}
