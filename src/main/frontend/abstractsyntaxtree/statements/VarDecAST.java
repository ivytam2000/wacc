package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.Var_decl_statContext;
import frontend.abstractsyntaxtree.ArrayTypeAST;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.PairTypeAST;
import frontend.abstractsyntaxtree.Utils;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

public class VarDecAST extends Node {

  private final SymbolTable symtab;
  private final Node typeAST;
  private final String varName;
  private final AssignRHSAST assignRHS;
  private final Var_decl_statContext ctx;

  public VarDecAST(SymbolTable symtab, Node typeAST,
      String varName, AssignRHSAST assignRHS, Var_decl_statContext ctx) {
    super();
    this.symtab = symtab;
    this.typeAST = typeAST;
    this.varName = varName;
    this.ctx = ctx;
    this.assignRHS = assignRHS;
  }

  @Override
  public void check() {
    if (typeAST instanceof PairTypeAST) {
      checkAsPairType();
    } else if (typeAST instanceof ArrayTypeAST) {
      checkAsArrayType();
    } else {
      String typeName = typeAST.getIdentifier().getType().getTypeName();
      Identifier typeID = symtab.lookupAll(typeName);
      Identifier variable = symtab.lookup(varName);

      if (typeID == null || !(typeID instanceof TypeID)) {
        // Check if the identifier returned has a known type
        SemanticErrorCollector.addUnknownType(typeName, ctx.getStart().getLine(),
            ctx.getStart().getCharPositionInLine());
      } else if (variable != null && !(variable instanceof FuncID)) {
        SemanticErrorCollector.addSymbolAlreadyDefined(varName, ctx.getStart().getLine(),
            ctx.getStart().getCharPositionInLine());
      }
      Utils.typeCompat(ctx.type(), ctx.assignRHS(), typeAST, assignRHS);
    }

    symtab.add(varName, typeAST.getIdentifier().getType());
    setIdentifier(typeAST.getIdentifier().getType());
  }

  private void checkAsPairType() {
    TypeID fstType = ((PairTypeAST) typeAST).getFst().getIdentifier().getType();
    TypeID sndType = ((PairTypeAST) typeAST).getSnd().getIdentifier().getType();
    TypeID assignType = assignRHS.getIdentifier().getType();

    if (assignType instanceof PairID) {
      PairID assignRHSAsPair = (PairID) assignType;

      verifyPairElemTypeOfRHS(fstType, assignRHSAsPair.getFstType());
      verifyPairElemTypeOfRHS(sndType, assignRHSAsPair.getSndType());

      // Infer the exact pair type of LHS from RHS
      TypeID assignRHSFstType = assignRHSAsPair.getFstType();
      TypeID assignRHSSndType = assignRHSAsPair.getSndType();
      PairID lhsType = new PairID(assignRHSFstType, assignRHSSndType);
      // Replace identifier of LHS with exact pair type
      typeAST.setIdentifier(lhsType);
    } else if (!(assignType instanceof NullID)) {
      SemanticErrorCollector.addIncompatibleType("pair", assignType.getTypeName(),
          varName, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }
  }

  private void checkAsArrayType() {
    TypeID assignType = assignRHS.getIdentifier().getType();
    if (assignType instanceof ArrayID) {
      if (!Utils.compareArrayTypes(typeAST.getIdentifier().getType(), assignType)) {
        String errorMsg = String.format("line %d:%d -- Array doesn't have consistent types, "
                + "index %d has expected type: %s but got actual type: %s",
            ctx.getStart().getLine(),
            ctx.getStart().getCharPositionInLine(), 0,
            typeAST.getIdentifier().getType().getTypeName(), assignType.getTypeName());
        SemanticErrorCollector.addError(errorMsg);
      }
    }
  }

  private boolean verifyPairElemTypeOfRHS(TypeID elemType, TypeID elemRHS) {
    if (elemType instanceof PairID) {
      if (!(elemRHS instanceof OptionalPairID)) {
        SemanticErrorCollector.addError("First of pair : Expected pair");
        return false;
      }
    } else if (elemType instanceof ArrayID) {
      if (!Utils.compareArrayTypes(elemType, elemRHS)) {
        SemanticErrorCollector.addError("First of pair : Expected array");
        return false;
      }
    } else {
      if (elemType != elemRHS) {
        SemanticErrorCollector.addError("First of pair : Types mismatch");
        return false;
      }
    }
    return true;
  }

  // TODO: FOR DEBUGGING.
  public Node getTypeAST() {
    return typeAST;
  }

  public AssignRHSAST getAssignRHS() {
    return assignRHS;
  }
}
