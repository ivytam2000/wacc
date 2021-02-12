package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;

public class PairElemTypeAST extends Node {

  public PairElemTypeAST(Identifier identifier) {
    super(identifier);
  }

  @Override
  public void check() {
  }
}
