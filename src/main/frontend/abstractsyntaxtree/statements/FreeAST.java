package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.ExprContext;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.pairs.PairElemTypeAST;
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
    List<Instr> instrs = new ArrayList<>(expr.toAssembly());
    TypeID exprType = expr.getIdentifier().getType();
    String label;
    if(exprType instanceof PairID){
      label = "p_free_pair";
    }else{
      // expr is of type Array
      label = "p_free_array";
    }
    BRANCH brInstr = new BRANCH(true, "", label);
    instrs.add(brInstr);
    return instrs;
  }
}
