package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;

public class CharLiterAST extends Node {

  private final String val; //For backend

  public CharLiterAST(SymbolTable symtab, String val) {
    super(symtab.lookupAll("char"));
    this.val = val;
  }

  @Override
  public void check() {}
}
