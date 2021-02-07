package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;

public class BoolLiterAST extends Node {

  public BoolLiterAST(Identifier identifier) {
    super(identifier);
  }

  @Override
  public void check() {
    //TODO: Change?
  }
}
