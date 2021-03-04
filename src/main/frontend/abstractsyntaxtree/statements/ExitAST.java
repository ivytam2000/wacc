package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.Exit_statContext;
import antlr.WaccParser.ExprContext;
import backend.instructions.AddrMode;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.expressions.IntLiterAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ExitID;
import frontend.symboltable.IntID;
import frontend.symboltable.TypeID;

import java.util.ArrayList;
import java.util.List;

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
    List<Instr> instrs = new ArrayList<>();
    expr.toAssembly();
    MOV movInstr = new MOV("", Instr.R0, AddrMode.buildReg(Instr.R4));
    instrs.add(movInstr);
    BRANCH brInstr = new BRANCH(true, "", "exit");
    instrs.add(brInstr);
    addToCurLabel(instrs);
  }
}
