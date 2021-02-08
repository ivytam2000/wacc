package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.PairID;

public class PairLiterAST extends Node {

  public PairLiterAST() {
    super(new PairID(null, null));
  }

  @Override
  public void check() {

  }
}
