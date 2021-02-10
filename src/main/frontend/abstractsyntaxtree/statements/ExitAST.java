package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExitAST extends Node {
  WaccParser.Exit_statContext ctx;
  private final Node expr;

  public ExitAST(Node expr, WaccParser.Exit_statContext ctx) {
    super(expr.getIdentifier());
    this.ctx = ctx;
    this.expr = expr;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {
    TypeID exprType = expr.getIdentifier().getType();
    WaccParser.ExprContext exprCtx = ctx.expr();
    if (!(exprType instanceof IntID)) {
      int line = exprCtx.getStart().getLine();
      int pos = exprCtx.getStart().getCharPositionInLine();
      SemanticErrorCollector.addIncompatibleType("int",
          exprType.getTypeName(), exprCtx.getText(), line, pos);
    }
  }
}
