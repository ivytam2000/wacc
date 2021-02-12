package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.Exit_statContext;
import antlr.WaccParser.ExprContext;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ExitID;
import frontend.symboltable.IntID;
import frontend.symboltable.TypeID;

public class ExitAST extends Node {
  Exit_statContext ctx;
  private final Node expr;

  public ExitAST(Node expr, Exit_statContext ctx) {
    super(new ExitID());
    this.ctx = ctx;
    this.expr = expr;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {
    TypeID exprType = expr.getIdentifier().getType();
    ExprContext exprCtx = ctx.expr();
    if (!(exprType instanceof IntID)) {
      int line = exprCtx.getStart().getLine();
      int pos = exprCtx.getStart().getCharPositionInLine();
      SemanticErrorCollector.addIncompatibleType(
          "int", exprType.getTypeName(), exprCtx.getText(), line, pos);
    }
  }
}
