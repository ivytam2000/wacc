package frontend.abstractsyntaxtree;

import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.PairID;
import frontend.symboltable.TypeID;

public class Utils {

  public static boolean typeCompat(TypeID t1, TypeID t2) {

    if (t1 instanceof PairID) {
      if (t2 instanceof PairID) {
        if (!comparePairTypes(t1, t2)) {
          SemanticErrorCollector.addError("LHS and RHS type are not compatible");
          return false;
        }
        return true;
      }
      SemanticErrorCollector.addError("LHS and RHS type are not compatible");
      return false;
    }

    if (t1 instanceof ArrayID) {
      if (t2 instanceof ArrayID) {
        if (!compareArrayTypes(t1, t2)) {
          SemanticErrorCollector.addError("LHS and RHS type are not compatible");
          return false;
        }
        return true;
      } else {
        if (((ArrayID) t1).getElemType().getType() != t2.getType()) {
          SemanticErrorCollector.addError("LHS and RHS type are not compatible");
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
    if (eLType instanceof PairID && eRType instanceof PairID) {
      return comparePairTypes(((PairID) eLType).getFstType(), ((PairID) eRType).getFstType())
          && comparePairTypes(((PairID) eLType).getSndType(), ((PairID) eRType).getSndType());
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
      return compareArrayTypes(((ArrayID) eLType).getElemType(), ((ArrayID) eRType).getElemType());
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
