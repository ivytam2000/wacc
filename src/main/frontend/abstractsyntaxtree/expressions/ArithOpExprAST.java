package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser;
import antlr.WaccParser.ArithOpExpr_1Context;
import antlr.WaccParser.ArithOpExpr_2Context;
import antlr.WaccParser.ExprContext;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import org.antlr.v4.runtime.ParserRuleContext;

public class ArithOpExprAST extends Node {

  private String operation;
  private final Node eL;
  private final Node eR;
  private final ExprContext ctx;

  public ArithOpExprAST(SymbolTable symtab, String operation, Node eL, Node eR,
      ExprContext ctx) {
    super(symtab.lookupAll("int"));
    this.eL = eL;
    this.eR = eR;
    assert (ctx instanceof ArithOpExpr_1Context
        || ctx instanceof ArithOpExpr_2Context);
    this.ctx = ctx;
  }

  @Override
  public void check() {
    if (eL.getIdentifier() != null && eR.getIdentifier() != null) {
      int fstStatPosition = 0;
      int sndStatPosition = 2;

      TypeID eLType = eL.getIdentifier().getType();
      TypeID eRType = eR.getIdentifier().getType();

      if (!(eLType instanceof IntID)) {
        SemanticErrorCollector
            .addIncompatibleType("int", eLType.getTypeName(),
                ctx.children.get(fstStatPosition).getText(),
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine());
      }
      if (!(eRType instanceof IntID)) {
        SemanticErrorCollector
            .addIncompatibleType("int", eRType.getTypeName(),
                ctx.children.get(sndStatPosition).getText(),
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine());
      }
    }
  }
}
