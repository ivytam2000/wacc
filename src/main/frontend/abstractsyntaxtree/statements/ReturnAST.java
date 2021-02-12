package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.Return_statContext;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.SymbolTable;

public class ReturnAST extends Node {

  private final SymbolTable symtab;
  private final Node expr;
  private final Return_statContext ctx;

  public ReturnAST(SymbolTable symtab, Node expr, Return_statContext ctx) {
    super(expr.getIdentifier());
    this.symtab = symtab;
    this.expr = expr;
    this.ctx = ctx;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {
    if (symtab.isTopLevel()) {
      SemanticErrorCollector.addGlobalReturnError(ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());
    }
  }
}
