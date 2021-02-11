package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class ArrayTypeAST extends Node {

  private SymbolTable symtab;
  private int dimensions;

  public ArrayTypeAST(Identifier identifier, SymbolTable symtab, int dimensions) {
    super(identifier);
    this.symtab = symtab;
    this.dimensions = dimensions;
  }

  public int getDimensions() {
    return dimensions;
  }

  @Override
  public void check() {

  }
}
