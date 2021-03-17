package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.AssignRHSContext;
import antlr.WaccParser.AssignLHSContext;
import backend.instructions.ADD;
import backend.instructions.AddrMode;
import backend.instructions.Condition;
import backend.instructions.Instr;
import backend.instructions.LDR;
import backend.instructions.MOV;
import backend.instructions.STR;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.abstractsyntaxtree.classes.ClassAttributeAST;
import frontend.abstractsyntaxtree.classes.ClassAttributeListAST;
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

  public String getRHSIdentName() {
    return rhsCtx.children.get(0).getText();
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
        return ((PairID) snd).getFstType() instanceof NullID && ((PairID) snd)
            .getSndType() instanceof NullID;
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
    // if assigning a class instance to another
    if (rhs.getIdentifier() instanceof ClassID && lhs
        .getIdentifier() instanceof ClassID) {
      ClassAttributeListAST lhsClassAttr = ((ClassID) lhs.getIdentifier())
          .getClassAttributes();
      List<ClassAttributeAST> lhsAttrs = lhsClassAttr.getAttributesList();
      String rhsInstanceName = getRHSIdentName();
      String lhsInstanceName = lhs.getIdentName();

      int classStackOffset = symtab.getStackOffset(rhsInstanceName);
      symtab.addOffset(lhsInstanceName, classStackOffset);

      for (ClassAttributeAST classAttr : lhsAttrs) {
        String rhsVarName = rhsInstanceName + "." + classAttr.getName();
        String lhsVarName = lhsInstanceName + "." + classAttr.getName();

        int tempStackOffset = symtab.getStackOffset(rhsVarName);
        symtab.addOffset(lhsVarName, tempStackOffset);
      }

      return;
    }

    // Evaluate the rhs to be assigned
    rhs.toAssembly();

    int bytes = rhs.getIdentifier().getType().getBytes();
    if (lhs.getAssignNode() instanceof ArrayElemAST ||
        lhs.getAssignNode() instanceof PairElemAST) {
      String sndReg = Instr.incDepth();
      // Evaluate lhs to get actual address to store result
      lhs.toAssembly();
      String fstReg = Instr.decDepth();
      Instr.addToCurLabel(new STR(bytes, Condition.NO_CON, fstReg,
          AddrMode.buildAddr(sndReg)));
    } else {
      int offset = symtab.getStackOffset(lhs.getIdentName());

      if (lhs.getIdentifier() instanceof ClassAttributeID) {
        if (symtab.isTopLevel()) {
          Instr.addToCurLabel(new STR(bytes, Condition.NO_CON, Instr.R4,
              AddrMode.buildAddrWithOffset(Instr.SP, offset)));
        } else {
          if (!symtab.getParent().getClassContext()) {
            Instr.addToCurLabel(new STR(bytes, Condition.NO_CON, Instr.R4,
                AddrMode.buildAddrWithOffset(Instr.SP, offset)));
          } else {
            String reg = Instr.incDepth();
            int instanceOffset = symtab.getStackOffset("object_addr");
            Instr.addToCurLabel(new LDR(reg,
                AddrMode.buildAddrWithOffset(Instr.SP, instanceOffset)));
            Instr.addToCurLabel(new ADD(false, reg, reg, AddrMode.buildImm(offset)));
            Instr.addToCurLabel(new STR(lhs.getIdentifier().getType().getBytes(), Condition.NO_CON, Instr.R4, AddrMode.buildAddr(reg)));

            Instr.decDepth();
          }
        }
      } else {
        // Regular variable
        Instr.addToCurLabel(new STR(bytes, Condition.NO_CON, Instr.R4,
            AddrMode.buildAddrWithOffset(Instr.SP, offset)));

        // Dynamic variable
        Identifier lhsType = symtab.lookup(lhs.getIdentName());
        if (lhsType instanceof VarID) {
          // Update symbol table about type so far
          TypeID rhsType = rhs.getIdentifier().getType();
          ((VarID) lhsType).setTypeSoFar(rhsType);

          List<Instr> instrs = new ArrayList<>();

          // Get addr into R4
          instrs.add(new ADD(false, Instr.R4, Instr.SP,
              AddrMode.buildImm(offset)));
          // Get type number and update (byte) at offset +4 of variable address
          List<TypeID> types = new ArrayList<>();
          types.add(rhsType);
          instrs.add(new MOV(Condition.NO_CON, Instr.R5,
              AddrMode.buildImm(Utils.getTypeNumber(types))));
          instrs.add(new STR(Instr.BYTE_SIZE, Condition.NO_CON, Instr.R5,
              AddrMode.buildAddrWithOffset(Instr.R4, Instr.WORD_SIZE)));

          Instr.addToCurLabel(instrs);
        }
      }
    }
  }
}
