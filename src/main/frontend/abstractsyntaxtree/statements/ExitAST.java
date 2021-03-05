package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.Exit_statContext;
import antlr.WaccParser.ExprContext;
import backend.instructions.*;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.expressions.IntLiterAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ExitID;
import frontend.symboltable.IntID;
import frontend.symboltable.TypeID;

import java.util.ArrayList;
import java.util.List;

import static backend.instructions.AddrMode.buildReg;
import static backend.instructions.Condition.NO_CON;
import static backend.instructions.Instr.addToCurLabel;

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

  @Override
  public void toAssembly() {
    // Evaluate the expression
    expr.toAssembly();
    List<Instr> instrs = new ArrayList<>();
    // Move the expression from R4 to R0
    MOV movInstr = new MOV(NO_CON, Instr.R0, buildReg(Instr.R4));
    instrs.add(movInstr);
    // Branch to exit label
    BRANCH brInstr = new BRANCH(true, NO_CON, Label.EXIT);
    instrs.add(brInstr);
    addToCurLabel(instrs);
  }
}
