package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.TypeID;

public class IfAST extends Node {

  private final Node expr;
  private final Node thenStat;
  private final Node elseStat;
  private final WaccParser.If_statContext ifCtx;
  private final WaccParser.ExprContext exprCtx;

  public IfAST(Node expr, Node thenStat, Node elseStat, WaccParser.If_statContext ctx) {
    // set identifier to be same as expressions but not sure if its correct?
    super(expr.getIdentifier());
    this.expr = expr;
    this.thenStat = thenStat;
    this.elseStat = elseStat;
    this.ifCtx = ctx;
    this.exprCtx = ctx.expr();
  }

  public Node getThenStat() {
    return thenStat;
  }

  public Node getElseStat() {
    return elseStat;
  }

  @Override
  public void check() {
    TypeID exprType = expr.getIdentifier().getType();
    if (!(exprType instanceof BoolID)) {
      SemanticErrorCollector.addIncompatibleType(
          "bool",
          expr.getIdentifier().getType().getTypeName(),
          exprCtx.getText(),
          exprCtx.getStart().getLine(),
          exprCtx.getStart().getCharPositionInLine());
    }
  }
}
