package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class PairElemAST extends Node {

  private SymbolTable symtab;
  private String pairName;
  private boolean first;
  private Node child;

  public PairElemAST(Identifier identifier, SymbolTable symtab, boolean first, Node child) {
    super(identifier);
    this.symtab = symtab;
    this.first = first;
    this.child = child;
  }

  @Override
  public void check() {
    child.check();
  }

}
