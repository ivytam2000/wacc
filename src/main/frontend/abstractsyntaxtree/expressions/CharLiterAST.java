package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;

public class CharLiterAST extends Node {

  private final char val; //For backend

  public CharLiterAST(SymbolTable symtab, char val) {
    super(symtab.lookupAll("char"));
    this.val = val;
  }

  @Override
  public void check() {}
}
