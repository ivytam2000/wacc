package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.ExprContext;
import backend.instructions.BRANCH;
import backend.instructions.CMP;
import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.TypeID;
import frontend.symboltable.UnknownID;

import java.util.ArrayList;
import java.util.List;

import static backend.BackEndGenerator.addToUsrDefFuncs;

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
    List<Instr> instrs = new ArrayList<>();
    List<Instr> whileOuterInstr = new ArrayList<>(expr.toAssembly());
    List<Instr> whileBodyInstr = new ArrayList<>(stat.toAssembly());
    whileOuterInstr.add(new CMP(Instr.R4,"#1"));
    // TODO: need to keep track of L labels?
    BRANCH beq = new BRANCH(false, "EQ", "L1");
    whileOuterInstr.add(beq);
    // Add to functions
    addToUsrDefFuncs("L0", whileOuterInstr);
    addToUsrDefFuncs("L1", whileBodyInstr);
    //TODO: how to handle labels?
    instrs.add(new BRANCH(false, "", "L0"));
    return instrs;
  }
}
