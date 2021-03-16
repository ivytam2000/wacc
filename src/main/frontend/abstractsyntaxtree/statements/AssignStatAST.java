package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.AssignRHSContext;
import antlr.WaccParser.AssignLHSContext;
import backend.instructions.ADD;
import backend.instructions.AddrMode;
import backend.instructions.Condition;
import backend.instructions.Instr;
import backend.instructions.LDR;
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
    // if assigning a class instance to another
    if (rhs.getIdentifier() instanceof ClassID && lhs.getIdentifier() instanceof ClassID) {
      ClassAttributeListAST lhsClassAttr = ((ClassID) lhs.getIdentifier()).getClassAttributes();
      List<ClassAttributeAST> lhsAttrs = lhsClassAttr.getAttributesList();
      String rhsInstanceName = getRHSIdentName();
      String lhsInstanceName = lhs.getIdentName();

      int classStackOffset = symtab.getStackOffset(rhsInstanceName);
      symtab.addOffset(lhsInstanceName, classStackOffset);

      for (ClassAttributeAST classAttr: lhsAttrs) {
        String rhsVarName = rhsInstanceName + "." + classAttr.getName();
        String lhsVarName = lhsInstanceName + "." + classAttr.getName();

        int tempStackOffset = symtab.getStackOffset(rhsVarName);
        symtab.addOffset(lhsVarName, tempStackOffset);
      }

      return;
    }

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
      int offset = symtab.getStackOffset(lhs.getIdentName());

      if (lhs.getIdentifier() instanceof ClassAttributeID) {
        if (symtab.isTopLevel()) {
          Instr.addToCurLabel(new STR(bytes, Condition.NO_CON, Instr.R4, AddrMode.buildAddrWithOffset(Instr.SP, offset)));
        } else {
          if (!symtab.getParent().getClassContext()) {
            Instr.addToCurLabel(new STR(bytes, Condition.NO_CON, Instr.R4, AddrMode.buildAddrWithOffset(Instr.SP, offset)));
          } else {
            String reg = Instr.incDepth();
            Instr.addToCurLabel(new LDR(reg, AddrMode.buildAddrWithOffset(Instr.SP, 4)));
            Instr.addToCurLabel(new ADD(false, reg, reg, AddrMode.buildImm(offset)));
            Instr.addToCurLabel(new STR(Instr.R4, AddrMode.buildAddr(reg)));
            Instr.decDepth();
          }
        }
      } else {
        // Regular variable
        Instr.addToCurLabel(new STR(bytes, Condition.NO_CON, Instr.R4, AddrMode.buildAddrWithOffset(Instr.SP, offset)));
      }
    }
  }
}
