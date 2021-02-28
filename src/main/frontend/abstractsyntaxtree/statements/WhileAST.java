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
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    List<Instr> whileOuterInstr = new ArrayList<>();
    List<Instr> whileBodyInstr = new ArrayList<>(stat.toAssembly());
    whileOuterInstr.add(new PUSH(Instr.LR));
    whileOuterInstr.addAll(expr.toAssembly());
    whileOuterInstr.add(new CMP(Instr.R4,"#1"));
    // TODO: need to keep track of L labels?
    BRANCH beq = new BRANCH(false, "EQ", "L1");
    whileOuterInstr.add(beq);
    whileOuterInstr.add(new POP(Instr.PC));
    // Add to functions
    addToUsrDefFuncs("L0", whileOuterInstr);
    addToUsrDefFuncs("L1", whileBodyInstr);
    //TODO: how to handle labels?
    instrs.add(new BRANCH(true, "", "L0"));
    return instrs;
  }
}
