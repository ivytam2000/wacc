package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.ExprContext;
import backend.BackEndGenerator;
import backend.instructions.*;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.PairID;
import frontend.symboltable.TypeID;

import java.util.ArrayList;
import java.util.List;

import static backend.instructions.AddrMode.buildReg;
import static backend.instructions.Condition.NO_CON;
import static backend.instructions.Instr.addToCurLabel;
import static backend.instructions.Label.P_FREE_ARRAY;
import static backend.instructions.Label.P_FREE_PAIR;

public class FreeAST extends Node {

  private final Node expr;
  private final ExprContext ctx;

  public FreeAST(Node expr, ExprContext ctx) {
    super(expr.getIdentifier());
    this.expr = expr;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    // Expression must be of type pair or array
    TypeID exprType = expr.getIdentifier().getType();
    if ((!(exprType instanceof PairID)) && (!(exprType instanceof ArrayID))) {
      SemanticErrorCollector.addIncompatibleType(
          "pair(T1, T2) or T[] (for " + "some T, T1, T2)",
          exprType.getTypeName(),
          ctx.getText(),
          ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());
    }
  }

  @Override
  public void toAssembly() {
    // Evaluate the expression
    expr.toAssembly();
    List<Instr> instrs = new ArrayList<>();
    // Move the value of the expression from R4 to R0
    instrs.add(new MOV(NO_CON, Instr.R0, buildReg(Instr.R4)));
    // Get the appropriate label to free the expression according to its type
    TypeID exprType = expr.getIdentifier().getType();
    String label = exprType instanceof PairID ? P_FREE_PAIR : P_FREE_ARRAY;
    BackEndGenerator.addToPreDefFuncs(label);
    // Branch to appropriate label
    BRANCH brInstr = new BRANCH(true, NO_CON, label);
    instrs.add(brInstr);
    addToCurLabel(instrs);
  }
}
