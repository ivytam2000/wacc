package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.ExprContext;
import backend.BackEndGenerator;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.PairID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.addToCurLabel;

public class FreeAST extends Node {

  private final Node expr;
  private final ExprContext ctx;
  private final SymbolTable symtab;

  public FreeAST(Node expr, ExprContext ctx, SymbolTable symtab) {
    super(expr.getIdentifier());
    this.expr = expr;
    this.ctx = ctx;

    this.symtab = symtab;
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
    expr.toAssembly();
    List<Instr> instrs = new ArrayList<>();
    TypeID exprType = expr.getIdentifier().getType();
    instrs.add(new MOV("", Instr.R0, Instr.R4));
    String label = exprType instanceof PairID ? "p_free_pair" : "p_free_array";
    BackEndGenerator.addToPreDefFuncs(label);
    BRANCH brInstr = new BRANCH(true, "", label);
    instrs.add(brInstr);
    addToCurLabel(instrs);
  }
}
