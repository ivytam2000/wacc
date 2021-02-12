package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;

public class IntLiterAST extends Node {

  private final boolean positive;
  private final String val;

  public IntLiterAST(SymbolTable symtab, boolean positive, String val) {
    super(symtab.lookupAll("int"));
    assert (super.identifier != null);
    this.positive = positive;
    this.val = val;
  }

  @Override
  public void check() {

  }
}
