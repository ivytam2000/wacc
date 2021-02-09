package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.BoolID;
import frontend.symboltable.CharID;
import frontend.symboltable.Identifier;
import frontend.symboltable.IntID;
import frontend.symboltable.PairID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.lang.reflect.Type;

public class BinOpExprAST extends Node {

  public static final int BOOL = 1;
  public static final int INT_CHAR = 2;
  public static final int ALL_TYPES = 3;

  private final String operation;
  private final int expectedExprTypes;
  private final Node eL;
  private final Node eR;

  public BinOpExprAST(SymbolTable symtab, int expectedExprTypes,
      String operation, Node eL, Node eR) {
    super(symtab.lookupAll("bool")); //BinOpExpr always has bool return type
    this.expectedExprTypes = expectedExprTypes;
    this.operation = operation;
    this.eL = eL;
    this.eR = eR;
  }

  @Override
  public void check() {
    boolean error = false;
    if (expectedExprTypes == BOOL) {
      error = !(eL.getIdentifier().getType() instanceof BoolID &&
          eR.getIdentifier().getType() instanceof BoolID);
    } else if (expectedExprTypes == INT_CHAR) {
      boolean isInt = eL.getIdentifier().getType() instanceof IntID &&
          eR.getIdentifier().getType() instanceof IntID;
      boolean isChar = eL.getIdentifier().getType() instanceof CharID &&
          eR.getIdentifier().getType() instanceof CharID;
      error = !(isInt || isChar);
    } else if (expectedExprTypes == ALL_TYPES) {
      TypeID eLType = eL.getIdentifier().getType();
      TypeID eRType = eR.getIdentifier().getType();
      if (eLType instanceof PairID && eRType instanceof PairID) {
        error = !comparePairTypes(eLType, eRType);
      } else if (eLType instanceof ArrayID && eRType instanceof ArrayID) {
        error = !compareArrayTypes(eLType, eRType);
      } else {
        error = !(eLType == eRType);
      }
    }
    if (error) {
      SemanticErrorCollector.addError("Binary Operator : Incompatible types.");
    }
  }

  private boolean comparePairTypes(TypeID eLType, TypeID eRType) {
    if (eLType instanceof PairID && eRType instanceof PairID) {
      return comparePairTypes(((PairID) eLType).getFstType(),
          ((PairID) eRType).getFstType()) && comparePairTypes(((PairID) eLType).getSndType(),
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
      return compareArrayTypes(((ArrayID) eLType).getElemType(), ((ArrayID) eRType).getElemType());
    } else {
      if (eLType instanceof PairID && eRType instanceof PairID) {
        return comparePairTypes(eLType, eRType);
      } else {
        return (eLType == eRType);
      }
    }
  }

}
