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
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>(expr.toAssembly());
    // TODO: Check if expr.toAssembly puts it into R4.
    instrs.add(new CMP(Instr.R4,"#0"));
    // TODO: need to keep track of L labels?
    BRANCH beq = new BRANCH(false, "EQ", "L0");
    // L0: elseStat.toAssembly()
    addToUsrDefFuncs("L0", elseStat.toAssembly());
    // TODO: L1 cleaning up routine?
    instrs.add(new BRANCH(false, "", "L1"));
    return instrs;
  }
}
