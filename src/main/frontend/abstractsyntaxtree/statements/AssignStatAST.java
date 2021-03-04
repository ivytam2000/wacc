package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.AssignRHSContext;
import antlr.WaccParser.AssignLHSContext;
import backend.BackEndGenerator;
import backend.instructions.AddrMode;
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

import java.util.ArrayList;
import java.util.List;

public class AssignStatAST extends Node {

  private final AssignRHSAST rhs;
  private final AssignLHSAST lhs;
  private final SymbolTable symtab;
  private final AssignRHSContext rhsCtx;
  private final AssignLHSContext lhsCtx;

  public AssignStatAST(AssignLHSContext lhsCtx, AssignRHSContext rhsCtx,
      AssignLHSAST lhs, AssignRHSAST rhs, SymbolTable symtab) {
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

  // TODO: Can we somehow collapse ArrayElem and PairElem case?
  @Override
  public void toAssembly() {
    rhs.toAssembly();
    int bytes = lhs.getIdentifier().getType().getBytes();
    if (lhs.getAssignNode() instanceof ArrayElemAST || lhs
        .getAssignNode() instanceof PairElemAST) {
      String sndReg = Instr.incDepth();
      lhs.toAssembly();
      String fstReg = Instr.decDepth();
      Instr.addToCurLabel(new STR(bytes, "" , fstReg, AddrMode.buildAddr(sndReg)));
    } else { // Regular variable
      Instr.addToCurLabel(new STR(bytes, "", Instr.R4, AddrMode.buildAddrWithOffset(Instr.SP,
          symtab.getStackOffset(lhs.getIdentName()))));
    }
  }
}
