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

  public VarDecAST(
      SymbolTable symtab,
      Node typeAST,
      String varName,
      AssignRHSAST assignRHS,
      Var_decl_statContext ctx) {
    super();
    this.symtab = symtab;
    this.typeAST = typeAST;
    this.varName = varName;
    this.ctx = ctx;
    this.assignRHS = assignRHS;
  }

  @Override
  public void check() {
    TypeID decType = typeAST.getIdentifier().getType();
    TypeID rhsType = assignRHS.getIdentifier().getType();
    String typeName = decType.getTypeName();
    int line = ctx.getStart().getLine();
    int pos = ctx.getStart().getCharPositionInLine();

    Identifier variable = symtab.lookup(varName);

    // Check if var is already declared unless it is a function name
    if (variable != null && !(variable instanceof FuncID)) {
      SemanticErrorCollector.addSymbolAlreadyDefined(varName, line, pos);
    }

    boolean checkTypes = true;

    // Check if type is a valid type if its not pair or array type (edge cases)
    if (!(typeAST instanceof PairTypeAST || typeAST instanceof ArrayTypeAST)) {
      Identifier typeID = symtab.lookupAll(typeName);
      if (!(typeID instanceof TypeID)) {
        checkTypes = false;
        SemanticErrorCollector.addUnknownType(typeName, line, pos);
      }
    }
    // Check if types are compatible
    if (checkTypes) {
      if (!Utils.typeCompat(decType, rhsType)) {
        SemanticErrorCollector.addIncompatibleType(
            decType.getTypeName(), rhsType.getTypeName(), varName, line, pos);
      }
    }
    symtab.add(varName, typeAST.getIdentifier().getType());
    setIdentifier(typeAST.getIdentifier().getType());
  }

  //  private void checkAsPairType() {
  //    TypeID fstType = ((PairTypeAST) typeAST).getFst().getIdentifier().getType();
  //    TypeID sndType = ((PairTypeAST) typeAST).getSnd().getIdentifier().getType();
  //    TypeID assignType = assignRHS.getIdentifier().getType();
  //
  //    if (assignType instanceof PairID) {
  //      PairID assignRHSAsPair = (PairID) assignType;
  //
  //      verifyPairElemTypeOfRHS(fstType, assignRHSAsPair.getFstType());
  //      verifyPairElemTypeOfRHS(sndType, assignRHSAsPair.getSndType());
  //    } else if (!(assignType instanceof NullID)) {
  //      SemanticErrorCollector.addIncompatibleType(
  //          "pair",
  //          assignType.getTypeName(),
  //          varName,
  //          ctx.getStart().getLine(),
  //          ctx.getStart().getCharPositionInLine());
  //    }
  //  }
  //
  //  private void checkAsArrayType() {
  //    TypeID assignType = assignRHS.getIdentifier().getType();
  //    if (assignType instanceof ArrayID) {
  //      if (!Utils.compareArrayTypes(typeAST.getIdentifier().getType(), assignType)) {
  //        String errorMsg =
  //            String.format(
  //                "line %d:%d -- Array doesn't have consistent types, "
  //                    + "expected type: %s but got actual type: %s",
  //                ctx.getStart().getLine(),
  //                ctx.getStart().getCharPositionInLine(),
  //                typeAST.getIdentifier().getType().getTypeName(),
  //                assignType.getTypeName());
  //        SemanticErrorCollector.addError(errorMsg);
  //      }
  //    }
  //  }
  //
  //  private void verifyPairElemTypeOfRHS(TypeID elemType, TypeID elemRHS) {
  //    if (elemType instanceof OptionalPairID) {
  //      if (!(elemRHS instanceof OptionalPairID)) {
  //        SemanticErrorCollector.addError("First of pair : Expected pair");
  //      }
  //    } else if (elemType instanceof ArrayID) {
  //      if (!Utils.compareArrayTypes(elemType, elemRHS)) {
  //        SemanticErrorCollector.addError("First of pair : Expected array");
  //      }
  //    } else {
  //      // Null rhs can match with any pair
  //      if (!(elemRHS instanceof NullID)) {
  //        if (elemType != elemRHS) {
  //          SemanticErrorCollector.addError("First of pair : Types mismatch");
  //        }
  //      }
  //    }
  //  }
  // TODO: FOR DEBUGGING.
  public Node getTypeAST() {
    return typeAST;
  }

  public AssignRHSAST getAssignRHS() {
    return assignRHS;
  }
}
