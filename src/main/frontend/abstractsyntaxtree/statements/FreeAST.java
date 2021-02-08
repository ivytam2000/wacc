package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.ArrayID;
import frontend.symboltable.PairID;
import frontend.symboltable.TypeID;

public class FreeAST extends Node {

  private final Node expr;

  // TODO: do we need an ExprAST class?
  public FreeAST(Node expr) {
    this.expr = expr;
  }

  @Override
  public void check() {
    // Expression must be of type pair or array
    TypeID exprType = expr.getIdentifier().getType();
    if ((!(exprType instanceof PairID)) && (!(exprType instanceof ArrayID))) {
      System.out.println("Error cannot free expression which is not of type " + "pair or array");
    }
  }
}
