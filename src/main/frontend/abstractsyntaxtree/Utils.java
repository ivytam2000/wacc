package frontend.abstractsyntaxtree;

import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.Identifier;
import frontend.symboltable.NullID;
import frontend.symboltable.PairID;
import frontend.symboltable.PairTypes;
import frontend.symboltable.TypeID;
import java.lang.reflect.Type;

public class Utils {

  public static boolean typeCompat(Node n1, Node n2) {
    assert (n1 != null);
    assert (n2 != null);

    boolean nodeIsNull = false;
    if (n1.getIdentifier() == null) {
      //TODO: Fix error message
      SemanticErrorCollector.addError("typeCompat : LHS type unknown");
      nodeIsNull = true;
    }
    if (n2.getIdentifier() == null) {
      //TODO: Fix error message
      SemanticErrorCollector.addError("typeCompat : RHS type unknown");
      nodeIsNull = true;
    }
    if (nodeIsNull) {
      return false;
    }

    TypeID t1 = n1.getIdentifier().getType();
    TypeID t2 = n2.getIdentifier().getType();
    assert (t1 != null);
    assert (t2 != null);

    if (t1 instanceof PairID) {
      if (t2 instanceof PairTypes) {
        if (comparePairTypes(t1, t2)) {
          return true;
        }
      }
      SemanticErrorCollector.addError("LHS and RHS type are not compatible");
      return false;
    }

    if (t1 instanceof ArrayID) {
      if (t2 instanceof ArrayID) {
        if (compareArrayTypes(t1, t2)) {
          return true;
        }
        SemanticErrorCollector.addError("LHS and RHS type are not compatible");
        return false;
      } else {
        System.exit(9);
        if (((ArrayID) t1).getElemType().getType() != t2.getType()) {
          SemanticErrorCollector
              .addError("LHS and RHS type are not compatible");
          return false;
        }
        return true;
      }
    }

    if (!(t1.getTypeName().equals((t2.getTypeName())))) {
      SemanticErrorCollector.addError("LHS and RHS type are not compatible");
      return false;
    }
    return true;
  }

  public static boolean comparePairTypes(TypeID eLType, TypeID eRType) {
    if (eLType instanceof PairID && eRType instanceof NullID) {
      return true;
    }
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

  public static boolean compareArrayTypes(TypeID eLType, TypeID eRType) {
    if (eLType instanceof ArrayID && eRType instanceof ArrayID) {
      return compareArrayTypes(((ArrayID) eLType).getElemType(),
          ((ArrayID) eRType).getElemType());
    } else {
      if (eLType instanceof PairID && eRType instanceof PairID) {
        return comparePairTypes(eLType, eRType);
      } else {
        if (eRType == null) {
          return true;
        }
        return (eLType == eRType);
      }
    }
  }
}
