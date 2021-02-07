package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.IntID;

public class IntLiterAST extends Node {
  private final long val;

  public IntLiterAST(Identifier identifier, long val) {
    super(identifier);
    this.val = val;
  }

  @Override
  public void check() {
    if (IntID.MIN > val || IntID.MAX < val) {
      //TODO: Change error message
      System.err.println("Val is not in range");
    }
  }
}
