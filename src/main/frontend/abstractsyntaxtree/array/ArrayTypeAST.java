package frontend.abstractsyntaxtree.array;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;

public class ArrayTypeAST extends Node {

  private final int dimensions;

  public ArrayTypeAST(Identifier identifier, int dimensions) {
    super(identifier);
    this.dimensions = dimensions;
  }

  //For future use in code generation
  public int getDimensions() {
    return dimensions;
  }

  @Override
  public void check() {

  }
}
