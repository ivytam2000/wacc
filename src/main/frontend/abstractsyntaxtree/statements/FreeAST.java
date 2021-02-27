package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.ExprContext;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.PairID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

import java.util.ArrayList;
import java.util.List;

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
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    // load the offset
    // TODO: how to get the variable name ??
    // offset = symtab.getStackOffset(expVarName)
    // LDR ldrInstr = new LDR(4, "", Instr.R4,Instr.SP, offset)
    BRANCH brInstr = new BRANCH(true, "", "p_print_reference");
    instrs.add(brInstr);
    return instrs;
  }
}
