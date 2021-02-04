package frontend.abstractsyntaxtree;

import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class FuncAST extends Node {

  public FuncAST(Identifier identifier, SymbolTable currSymTab, Node parentNode) {
    super(identifier, currSymTab, parentNode);
  }

  @Override
  public void check() {
  }
}
