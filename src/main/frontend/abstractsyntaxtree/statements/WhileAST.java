package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.ExprContext;
import backend.Utils;
import backend.instructions.*;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import frontend.symboltable.UnknownID;

import java.util.ArrayList;
import java.util.List;

import static backend.BackEndGenerator.addToUsrDefFuncs;
import static backend.instructions.Instr.*;

public class WhileAST extends Node {
  private final Node expr;
  private final Node stat;
  private final ExprContext exprCtx;
  private final SymbolTable symtab;

  public WhileAST(Node expr, Node stat, ExprContext exprCtx,
      SymbolTable symtab) {
    super(expr.getIdentifier());
    this.expr = expr;
    this.stat = stat;
    this.exprCtx = exprCtx;
    this.symtab = symtab;
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
  public void toAssembly() {
    /* Create a new label for nextStat which will include the evaluation of the
     * while condition expression and the instructions after the while loop */
    String nextStatLabel = getNextLabel();
    /* Create a new label fo the body of the loop */
    String bodyLabel = getNextLabel();

    /* Branch to this the nextStat label */
    addToCurLabel(new BRANCH(false, "", nextStatLabel));

    /* Set CurLabel to nextStat label */
    setCurLabel(nextStatLabel);
    expr.toAssembly();
    /* Test if the expression is true if it is we branch to a new label which
       will include the instructions for the body of the while loop */
    addToCurLabel(new CMP(Instr.R4,"#1"));
    addToCurLabel(new BRANCH(false, "EQ", bodyLabel));

    /* Set current label to the body label */
    setCurLabel(bodyLabel);
    // Add body label to label order list as it should be printed before the
    // nextStat label
    addToLabelOrder(bodyLabel);
    stat.toAssembly();

    /*Set current label back to the nextStat label and add it to the label
     * order */
    setCurLabel(nextStatLabel);
    addToLabelOrder(nextStatLabel);
  }
}
