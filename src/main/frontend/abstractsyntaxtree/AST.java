package frontend.abstractsyntaxtree;

import frontend.symboltable.SymbolTable;

public class AST extends Node {

  public AST(SymbolTable topLevelSymTab) {
    super(topLevelSymTab, null); //Parent is null
  }

  @Override
  public void check() {

  }
}
