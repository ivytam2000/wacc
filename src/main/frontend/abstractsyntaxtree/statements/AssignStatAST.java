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

      if (lhsType instanceof PairID && rhsType instanceof PairID) {
        // Within this branch, we know that lhs is definitely a pair

        Node assignNode = lhs.getAssignNode();

        if (assignNode instanceof PairElemAST) { // Assigning to pairElem
          boolean isFirst = ((PairElemAST) assignNode).isFirst();
          PairID lhsPairType = (PairID) lhsType;

          if (isPairOfNulls(lhsPairType)) {
            lhs.setIdentifier(rhsType);
            PairID fullPairType = (PairID) symtab.lookupAll(varName);
            if (isFirst) {
              fullPairType.setFst(rhsType);
            } else {
              fullPairType.setSnd(rhsType);
            }
            symtab.replaceType(varName, fullPairType);
          }

        } else { // Assigning to full pair

          if (isUninitialisedNestedPair((PairID) lhsType)) {
            if (isNestedPair((PairID) rhsType)) {
              lhsType = rhsType;
              lhs.setIdentifier(lhsType);
              symtab.replaceType(varName, lhsType);
            }
          }

        }
      }

      if (!Utils.typeCompat(lhsType, rhsType)) { // types don't match
        String prefix = "";
        Node lhsAssignNode = lhs.getAssignNode();
        if (lhsAssignNode instanceof PairElemAST) {
          prefix = ((PairElemAST) lhsAssignNode).isFirst() ? "fst " : "snd ";
        }
        SemanticErrorCollector.addIncompatibleType(
            lhsType.getTypeName(),
            rhsType.getTypeName(),
            prefix + varName,
            lhsLine,
            rhsCtx.getStart().getCharPositionInLine());
      }
    }
  }

  private boolean isPairOfNulls(PairID type) {
    TypeID fst = type.getFstType();
    TypeID snd = type.getSndType();
    return (fst instanceof NullID && snd instanceof NullID);
  }

  // pair(pair(null, null), pair(null, null))
  private boolean isUninitialisedNestedPair(PairID type) {
    TypeID fst = type.getFstType();
    TypeID snd = type.getSndType();
    if (fst instanceof PairID && snd instanceof PairID) {
      if (((PairID) fst).getFstType() instanceof NullID && ((PairID) fst)
          .getSndType() instanceof NullID) {
        if (((PairID) snd).getFstType() instanceof NullID && ((PairID) snd)
            .getSndType() instanceof NullID) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isNestedPair(PairID type) {
    TypeID fst = type.getFstType();
    TypeID snd = type.getSndType();
    return (fst instanceof PairID && snd instanceof PairID);
  }

  @Override
  public void toAssembly() {
    // Evaluate the rhs to be assigned
    rhs.toAssembly();

    int bytes = lhs.getIdentifier().getType().getBytes();
    if (lhs.getAssignNode() instanceof ArrayElemAST ||
        lhs.getAssignNode() instanceof PairElemAST) {
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
