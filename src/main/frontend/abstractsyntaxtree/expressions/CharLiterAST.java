package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.SymbolTable;

public class CharLiterAST extends Node {

  public final char val;

  public CharLiterAST(SymbolTable symtab, char val) {
    super(symtab.lookupAll("char"));
    assert (super.identifier != null);
    this.val = val;
  }

  @Override
  public void check() {

  }
}
