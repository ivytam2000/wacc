package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;

public class ArrayTypeAST extends Node {

  private int dimensions;

  public ArrayTypeAST(Identifier identifier, int dimensions) {
    super(identifier);
    this.dimensions = dimensions;
  }

  // for future use in code generation
  public int getDimensions() {
    return dimensions;
  }

  @Override
  public void check() {

  }
}
