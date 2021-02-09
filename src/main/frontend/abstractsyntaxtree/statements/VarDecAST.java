package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.PairTypeAST;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

import static frontend.abstractsyntaxtree.statements.AssignStatAST.typeCompat;

public class VarDecAST extends Node {

  private final SymbolTable symtab;
  private final Node typeAST;
  private final String varName;
  private final AssignRHSAST assignRHS;

  public VarDecAST(SymbolTable symtab, Node typeAST, String varName,
      AssignRHSAST assignRHS) {
    super();
    this.symtab = symtab;
    this.typeAST = typeAST;
    this.varName = varName;
    this.assignRHS = assignRHS;
  }

  @Override
  public void check() {

    if (typeAST instanceof PairTypeAST) {
      TypeID fstType = ((PairTypeAST) typeAST).getFst().getIdentifier()
          .getType();
      TypeID sndType = ((PairTypeAST) typeAST).getSnd().getIdentifier()
          .getType();

      TypeID assignType = assignRHS.getIdentifier().getType();
      if (assignType instanceof PairID) {
        PairID assignPair = (PairID) assignType;
        boolean correctArr = true;
        if (fstType instanceof ArrayID) {
          correctArr = compareArrayTypes(fstType, assignPair.getFstType());
        }
        if (sndType instanceof ArrayID) {
          correctArr = correctArr && compareArrayTypes(sndType,
              assignPair.getSndType());
        }
        if (!correctArr) {
          SemanticErrorCollector.addError("Expected array");
        } else {
          if (!(fstType.getTypeName()
              .equals(assignPair.getFstType().getTypeName())
              && sndType.getTypeName()
              .equals(assignPair.getSndType().getTypeName()))) {
            SemanticErrorCollector.addError("Pair types do not match with rhs");
          }
        }

      } else {
        SemanticErrorCollector.addError(
            "Incompatible types: expected pair, " + "but actual:" + assignType
                .getTypeName());
      }
      PairID pairType = new PairID(fstType, sndType);
      symtab.add(varName, pairType);
    } else {
      String typeName = typeAST.getIdentifier().getType().getTypeName();
      Identifier typeID = symtab.lookupAll(typeName);
      Identifier variable = symtab.lookup(varName);

      if (typeID == null) {
        // check if the identifier returned is a known type/identifier
        SemanticErrorCollector.addError("Unknown type " + typeName);
      } else if (!(typeID instanceof TypeID)) {
        // check if the identifier is a type
        SemanticErrorCollector.addError(typeName + "is not a type");
      } else if (variable != null) {
        SemanticErrorCollector.addError(varName + "is already declared");
      } else {
        TypeID rhsType = assignRHS.getIdentifier().getType();
        if (typeCompat((TypeID) typeID, rhsType)) {
          // create variable identifier
          VariableID varID = new VariableID((TypeID) typeID, varName);
          // add to symbol table
          symtab.add(varName, varID);
          setIdentifier(varID);
        }
      }
    }
  }

  private boolean comparePairTypes(TypeID eLType, TypeID eRType) {
    if (eLType instanceof PairID && eRType instanceof PairID) {
      return comparePairTypes(((PairID) eLType).getFstType(),
          ((PairID) eRType).getFstType())
          && comparePairTypes(((PairID) eLType).getSndType(),
          ((PairID) eRType).getSndType());
    } else {
      if (eLType instanceof ArrayID && eRType instanceof ArrayID) {
        return compareArrayTypes(eLType, eRType);
      } else {
        return (eLType == eRType);
      }
    }
  }

  private boolean compareArrayTypes(TypeID eLType, TypeID eRType) {
    if (eLType instanceof ArrayID && eRType instanceof ArrayID) {
      return compareArrayTypes(((ArrayID) eLType).getElemType(),
          ((ArrayID) eRType).getElemType());
    } else {
      if (eLType instanceof PairID && eRType instanceof PairID) {
        return comparePairTypes(eLType, eRType);
      } else {
        return (eLType == eRType);
      }
    }
  }
}
