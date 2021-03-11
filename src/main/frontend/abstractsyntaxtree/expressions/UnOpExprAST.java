package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser.UnaryOperContext;
import backend.BackEndGenerator;
import backend.instructions.AddrMode;
import backend.instructions.BRANCH;
import backend.instructions.Condition;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.Label;
import backend.instructions.ORR;
import backend.instructions.SUB;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.BoolID;
import frontend.symboltable.CharID;
import frontend.symboltable.Identifier;
import frontend.symboltable.IntID;
import frontend.symboltable.StringID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.ArrayList;
import java.util.List;

import static backend.instructions.Instr.FALSE_VAL;
import static backend.instructions.Instr.TRUE_VAL;
import static backend.instructions.Instr.WORD_SIZE;
import static backend.instructions.Instr.addToCurLabel;

public class UnOpExprAST extends Node {

  private final SymbolTable symbtab;
  private final UnaryOperContext ctx;
  private final Node exprAST;

  public UnOpExprAST(SymbolTable symbtab, Node exprAST, UnaryOperContext ctx) {
    super();
    this.symbtab = symbtab;
    this.ctx = ctx;
    this.exprAST = exprAST;
  }

  // Different unOp defined for different types
  @Override
  public void check() {
    TypeID exprType = exprAST.getIdentifier().getType();
    Identifier unOpExprType = null;
    boolean error = false;

    if (ctx.NOT() != null) { // NOT defined for bool only
      if (!(exprType instanceof BoolID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("bool");

    } else if (ctx.MINUS() != null) { // MINUS defined for int only
      if (!(exprType instanceof IntID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("int");

    } else if (ctx.CHR() != null) { // CHR defined for int only
      if (!(exprType instanceof IntID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("char");

    } else if (ctx.LEN() != null) { // LEN defined for string and array
      if (!(exprType instanceof StringID || exprType instanceof ArrayID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("int");

    } else if (ctx.ORD() != null) { // ORD defined for char only
      if (!(exprType instanceof CharID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("int");
    } else if (ctx.BIT_NOT() != null) { // BIT_NOT defined for int only
      if (!(exprType instanceof IntID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("int");
    }

    if (error) {
      SemanticErrorCollector
          .addIncompatibleType(unOpExprType.getType().getTypeName(),
              exprType.getTypeName(),
              ctx.getText(), ctx.getStart().getLine(),
              ctx.getStart().getCharPositionInLine());
    }

    setIdentifier(unOpExprType);
  }

  @Override
  public void toAssembly() {
    // op exprAST
    // Evaluate exprAST, then op

    // Evaluate exprAST
    String targetReg = Instr.getTargetReg();
    exprAST.toAssembly();

    // Evaluate op
    List<Instr> instrs = new ArrayList<>();
    if (ctx.NOT() != null) {
      // Use XOR to flip bits
      instrs.add(new ORR(true, targetReg, AddrMode.buildImm(TRUE_VAL)));
    } else if (ctx.MINUS() != null) {
      // Reverse subtract
      instrs
          .add(new SUB(true, true, targetReg, targetReg, AddrMode.buildImm(0)));
      // Check for overflow
      BackEndGenerator.addToPreDefFuncs(Label.P_THROW_OVERFLOW_ERROR);
      instrs.add(new BRANCH(true, Condition.VS, Label.P_THROW_OVERFLOW_ERROR));
    } else if (ctx.LEN() != null) {
      // Length of array stored at 0th index of its corresponding memory addr
      instrs.add(new LDR(WORD_SIZE, Condition.NO_CON, targetReg,
          AddrMode.buildAddr(targetReg)));
    } else if (ctx.BIT_NOT() != null) {
      instrs
          .add(new ORR(true, targetReg, AddrMode.buildImm(Integer.MAX_VALUE)));
    }
    /*
    No need additional instructions for chr and ord as long as the correct value
    is loaded when evaluating expression.
    */

    addToCurLabel(instrs);
  }
}
