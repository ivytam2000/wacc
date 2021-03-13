package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.AssignRHSContext;
import antlr.WaccParser.AssignLHSContext;
import backend.instructions.AddrMode;
import backend.instructions.Condition;
import backend.instructions.Instr;
import backend.instructions.STR;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.abstractsyntaxtree.expressions.ArrayElemAST;
import frontend.abstractsyntaxtree.pairs.PairElemAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

public class AssignStatAST extends Node {

  private final AssignRHSAST rhs;
  private final AssignLHSAST lhs;
  private final SymbolTable symtab;
  private final AssignRHSContext rhsCtx;
  private final AssignLHSContext lhsCtx;

  public AssignStatAST(
      AssignLHSContext lhsCtx,
      AssignRHSContext rhsCtx,
      AssignLHSAST lhs,
      AssignRHSAST rhs,
      SymbolTable symtab) {
    this.rhs = rhs;
    this.lhs = lhs;
    this.lhsCtx = lhsCtx;
    this.rhsCtx = rhsCtx;
    this.symtab = symtab;
  }

  @Override
  public void check() {
    String varName = lhs.getIdentName();
    Identifier var = symtab.lookupAll(varName);

    int lhsLine = lhsCtx.getStart().getLine();
    int lhsPos = lhsCtx.getStart().getCharPositionInLine();

    if (var == null) { // Undefined variable
      SemanticErrorCollector.addVariableUndefined(varName, lhsLine, lhsPos);
    } else {
      TypeID lhsType = lhs.getIdentifier().getType();
      TypeID rhsType = rhs.getIdentifier().getType();

      if (!Utils.typeCompat(lhsType, rhsType)) { // types don't match
        SemanticErrorCollector.addIncompatibleType(
            lhsType.getTypeName(),
            rhsType.getTypeName(),
            varName,
            lhsLine,
            rhsCtx.getStart().getCharPositionInLine());
      }
    }
  }

  @Override
  public void toAssembly() {
    // Evaluate the rhs to be assigned
    rhs.toAssembly();

    int bytes = lhs.getIdentifier().getType().getBytes();
    if (lhs.getAssignNode() instanceof ArrayElemAST || lhs.getAssignNode() instanceof PairElemAST) {
      String sndReg = Instr.incDepth();
      // Evaluate lhs to get actual address to store result
      lhs.toAssembly();
      String fstReg = Instr.decDepth();
      Instr.addToCurLabel(new STR(bytes, Condition.NO_CON, fstReg, AddrMode.buildAddr(sndReg)));
    } else {
      // Regular variable
      int offset = symtab.getStackOffset(lhs.getIdentName());
      Instr.addToCurLabel(new STR(bytes, Condition.NO_CON, Instr.R4, AddrMode.buildAddrWithOffset(Instr.SP, offset)));
    }
  }
}
