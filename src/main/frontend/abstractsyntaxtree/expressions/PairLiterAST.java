package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.NullID;

public class PairLiterAST extends Node {

  public PairLiterAST() {
    super(new NullID());
  }

  @Override
  public void check() {

  }
}
