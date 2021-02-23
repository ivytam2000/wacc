package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.ExprContext;
import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.TypeID;
import frontend.symboltable.UnknownID;
import java.util.List;

public class WhileAST extends Node {
  private final Node expr;
  private final Node stat;
  private final ExprContext exprCtx;

  public WhileAST(Node expr, Node stat, ExprContext exprCtx) {
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
      if (!(exprType instanceof UnknownID || exprType instanceof BoolID)) {
        SemanticErrorCollector.addIncompatibleType(
            "bool",
            exprType.getTypeName(),
            exprCtx.getText(),
            exprCtx.getStart().getLine(),
            exprCtx.getStart().getCharPositionInLine());
      }
    }
  }

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
