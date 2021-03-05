package frontend.abstractsyntaxtree.array;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;

public class ArrayTypeAST extends Node {

  private final int dimensions;

  public ArrayTypeAST(Identifier identifier, int dimensions) {
    super(identifier);
    this.dimensions = dimensions;
  }

  @Override
  public void check() {}

  @Override
  public void toAssembly() {}
}
