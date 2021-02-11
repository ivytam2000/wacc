package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.TypeID;

public class WhileAST extends Node {
  private final Node expr;
  private final Node stat;
  private final WaccParser.ExprContext exprCtx;

  public WhileAST(Node expr, Node stat, WaccParser.ExprContext exprCtx) {
    super(expr.getIdentifier());
    this.expr = expr;
    this.stat = stat;
    this.exprCtx = exprCtx;
  }

  public Node getStat() {
    return stat;
  }

  @Override
  public void check() {
    if (expr.getIdentifier() != null) {
      TypeID exprType = expr.getIdentifier().getType();
      if (!(exprType instanceof BoolID)) {
        SemanticErrorCollector.addIncompatibleType(
            "bool",
            exprType.getTypeName(),
            exprCtx.getText(),
            exprCtx.getStart().getLine(),
            exprCtx.getStart().getCharPositionInLine());
      }
    }
  }
}
