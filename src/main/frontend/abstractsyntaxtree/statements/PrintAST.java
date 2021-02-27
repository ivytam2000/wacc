package frontend.abstractsyntaxtree.statements;

import backend.Utils;
import backend.instructions.BRANCH;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.expressions.BoolLiterAST;
import frontend.abstractsyntaxtree.expressions.CharLiterAST;
import frontend.abstractsyntaxtree.expressions.IntLiterAST;
import frontend.symboltable.*;

import java.util.ArrayList;
import java.util.List;

import static backend.Utils.getAssignValue;

public class PrintAST extends Node {

  private final Node expr;
  private final SymbolTable symtab;

  public PrintAST(Node expr, SymbolTable symtab) {
    super(expr.getIdentifier());
    this.expr = expr;
    this.symtab = symtab;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {}

  @Override
  public List<Instr> toAssembly() {
    List<Instr> instrs = new ArrayList<>();
    TypeID exprType = expr.getIdentifier().getType();
    BRANCH brInstr = null;

    if (exprType instanceof IntID) {
      // TODO: Not sure if best way is to cast?
      IntLiterAST intExpr = (IntLiterAST) expr;
      // Load int into R4
      LDR ldr = new LDR(4, "", Instr.R4, "=" + intExpr.getValue());
      instrs.add(ldr);
      brInstr = new BRANCH(true, "", "p_print_int");
    } else if (exprType instanceof BoolID) {
      BoolLiterAST boolExpr = (BoolLiterAST) expr;
      // optimised a bit from ref compiler did not load to intermediate R4 reg.
      MOV movExpr = new MOV("", Instr.R0, boolExpr.getVal());
      instrs.add(movExpr);
      brInstr = new BRANCH(true, "", "p_print_bool");
    } else if (exprType instanceof CharID) {
      CharLiterAST charExpr = (CharLiterAST) expr;
      // optimised a bit from ref compiler did not load to intermediate R4 reg.
      MOV movExpr = new MOV("", Instr.R0, charExpr.getVal());
      instrs.add(movExpr);
      brInstr = new BRANCH(true, "", "putchar");
    } else if (exprType instanceof StringID) {
      // Load label of string to register 4
      // TODO: how to get label?
      brInstr = new BRANCH(true, "", "p_print_string");
    } else if (exprType instanceof PairID || exprType instanceof ArrayID) {
      // load the offset
      // TODO: how to get the variable name ??
      // offset = symtab.getStackOffset(expVarName)
      // LDR ldrInstr = new LDR(4, "", Instr.R4,Instr.SP, offset)
      brInstr = new BRANCH(true, "", "p_print_reference");
    }

    MOV movInstr = new MOV("", Instr.R0, Instr.R4);
    instrs.add(movInstr);
    instrs.add(brInstr);
    return instrs;
  }
}
