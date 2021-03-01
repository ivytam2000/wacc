package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.ExprContext;
import backend.BackEndGenerator;
import backend.instructions.BRANCH;
import backend.instructions.CMP;
import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.TypeID;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

import static backend.BackEndGenerator.addToUsrDefFuncs;
import static backend.instructions.Instr.*;

public class IfAST extends Node {

  private final Node expr;
  private final Node thenStat;
  private final Node elseStat;
  private final ExprContext exprCtx;

  public IfAST(Node expr, Node thenStat, Node elseStat, ExprContext ctx) {
    // Set identifier to be same as expressions
    super(expr.getIdentifier());
    this.expr = expr;
    this.thenStat = thenStat;
    this.elseStat = elseStat;
    this.exprCtx = ctx;
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

  @Override
  public void toAssembly() {
    /* Evaluate the boolean expression */
    expr.toAssembly();

    /* This ensures that the nextStatLabel is always after the elseStatLabel */
    String elseStatLabel = getNextLabel();
    String nextStatLabel = getNextLabel();

    /* Test the boolean expression if it evaluates to false jump to elseStat
    label */
    addToCurLabel(new CMP(Instr.R4,"#0"));
    addToCurLabel(new BRANCH(false, "EQ", elseStatLabel));

    /* Evaluate thenStat (true) body in current label */
    thenStat.toAssembly();
    // branch to the nextStatLabel to skip over the elseStatlabel (false body)
    addToCurLabel(new BRANCH(false, "", nextStatLabel));

    /* Evaluate the elseStat (false body) in a new label (elseStatLabel) */
    setCurLabel(elseStatLabel);
    elseStat.toAssembly();

    /* Create and set a new label for the next statements after the ifAST */
    setCurLabel(nextStatLabel);

    /* Add elseStatLabel and then nextStatLabel to labelOrder list
     * elseStatLabel must be before nextStatLabel so that it falls through */
    addToLabelOrder(elseStatLabel);
    addToLabelOrder(nextStatLabel);
  }
}
