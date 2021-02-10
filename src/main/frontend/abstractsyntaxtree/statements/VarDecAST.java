package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser;
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
  private final WaccParser.Var_decl_statContext ctx;

  public VarDecAST(SymbolTable symtab, Node typeAST,
      WaccParser.Var_decl_statContext ctx, AssignRHSAST assignRHS) {
    super();
    this.symtab = symtab;
    this.typeAST = typeAST;
    this.varName = ctx.IDENT().getText();
    this.ctx = ctx;
    this.assignRHS = assignRHS;
  }

  @Override
  public void check() {
    if (typeAST instanceof PairTypeAST) {
      TypeID fstType = ((PairTypeAST) typeAST).getFst().getIdentifier().getType();
      TypeID sndType = ((PairTypeAST) typeAST).getSnd().getIdentifier().getType();
      TypeID assignType = assignRHS.getIdentifier().getType();

      if (assignType instanceof PairID) {
        PairID assignRHSAsPair = (PairID) assignType;

        verifyPairElemTypeOfRHS(fstType, assignRHSAsPair.getFstType());
        verifyPairElemTypeOfRHS(sndType, assignRHSAsPair.getSndType());

        TypeID assignRHSFstType = assignRHSAsPair.getFstType();
        TypeID assignRHSSndType = assignRHSAsPair.getSndType();
        PairID lhsType = new PairID(assignRHSFstType, assignRHSSndType);
        typeAST.setIdentifier(lhsType);
      } else if (!(assignType instanceof NullID)) {
        SemanticErrorCollector.addError(
            "Incompatible types: expected pair, " + "but actual:" + assignType.getTypeName());
      }
    } else if (typeAST instanceof ArrayTypeAST) {
      TypeID assignType = assignRHS.getIdentifier().getType();
      if (assignType instanceof ArrayID) {
        if (!Utils.compareArrayTypes(typeAST.getIdentifier().getType(), assignType)) {
          SemanticErrorCollector.addError("Not same array type");
        }
      }
    } else {
      String typeName = typeAST.getIdentifier().getType().getTypeName();
      Identifier typeID = symtab.lookupAll(typeName);
      Identifier variable = symtab.lookup(varName);

      if (typeID == null) {
        // Check if the identifier returned has a known type
        SemanticErrorCollector.addError("Unknown type " + typeName);
      } else if (variable != null && !(variable instanceof FuncID)) {
        SemanticErrorCollector.addError(varName + " is already declared");
      } else if (!(typeID instanceof TypeID)) {
        // Check if the identifier is a type identifier
        SemanticErrorCollector.addError(typeName + "is not a type");
      }
      Utils.typeCompat(ctx.type(), ctx.assignRHS(), typeAST, assignRHS);
    }

    symtab.add(varName, typeAST.getIdentifier().getType());
    setIdentifier(typeAST.getIdentifier().getType());
  }

  private boolean verifyPairElemTypeOfRHS(TypeID elemType, TypeID elemRHS) {
    if (elemType instanceof PairID) {
      if (!(elemRHS instanceof PairTypes)) {
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
