package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.ExprContext;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.PairID;
import frontend.symboltable.TypeID;

public class FreeAST extends Node {

  private final Node expr;
  private final ExprContext ctx;

  // TODO: do we need an ExprAST class?
  public FreeAST(Node expr, ExprContext ctx) {
    super(expr.getIdentifier());
    this.expr = expr;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    // Expression must be of type pair or array
    TypeID exprType = expr.getIdentifier().getType();
    if ((!(exprType instanceof PairID)) && (!(exprType instanceof ArrayID))) {
      SemanticErrorCollector.addIncompatibleType(
          "pair(T1, T2) or T[] (for " + "some T, T1, T2)",
          exprType.getTypeName(),
          ctx.getText(),
          ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());
    }
  }
}
