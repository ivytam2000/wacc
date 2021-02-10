package frontend.abstractsyntaxtree;

import frontend.abstractsyntaxtree.statements.ExitAST;
import frontend.abstractsyntaxtree.statements.IfAST;
import frontend.abstractsyntaxtree.statements.ReturnAST;
import frontend.abstractsyntaxtree.statements.SequenceAST;
import frontend.abstractsyntaxtree.statements.WhileAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.NullID;
import frontend.symboltable.PairID;
import frontend.symboltable.OptionalPairID;
import frontend.symboltable.TypeID;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;

public class Utils {

  public static boolean typeCompat(ParserRuleContext n1Ctx,
      ParserRuleContext n2Ctx, Node n1, Node n2) {
    assert (n1 != null);
    assert (n2 != null);
    int n1Line = n1Ctx.getStart().getLine();
    int n1Pos = n1Ctx.getStart().getCharPositionInLine();
    String n1Token = n1Ctx.getStart().getText();
    int n2Line = n2Ctx.getStart().getLine();
    int n2Pos = n2Ctx.getStart().getCharPositionInLine();
    String n2Token = n2Ctx.getStart().getText();

    boolean nodeIsNull = false;
    if (n1.getIdentifier() == null) {
      SemanticErrorCollector.addIncompatibleType("unknown", "unknown", n1Token, n1Line, n1Pos);
      nodeIsNull = true;
    }
    if (n2.getIdentifier() == null) {
      SemanticErrorCollector.addIncompatibleType("unknown", "unknown", n2Token, n2Line, n2Pos);
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
      if (t2 instanceof OptionalPairID) {
        if (comparePairTypes(t1, t2)) {
          return true;
        }
      }
      SemanticErrorCollector
          .addIncompatibleType(t1.getTypeName(), t2.getTypeName(), n2Token, n2Line, n2Pos);
      return false;
    }

    if (t1 instanceof ArrayID) {
      if (t2 instanceof ArrayID) {
        if (compareArrayTypes(t1, t2)) {
          return true;
        }
        SemanticErrorCollector
            .addIncompatibleType(t1.getTypeName(), t2.getTypeName(), n2Token, n2Line, n2Pos);
        return false;
      } else {
        TypeID t1AsArrayElemType = ((ArrayID) t1).getElemType().getType();
        if (t1AsArrayElemType != t2.getType()) {
          SemanticErrorCollector
              .addIncompatibleType(t1AsArrayElemType.getTypeName(), n2Token, t2.getTypeName(),
                  n2Line, n2Pos);
          return false;
        }
        return true;
      }
    }

    if (!(t1.getTypeName().equals((t2.getTypeName())))) {
      SemanticErrorCollector
          .addIncompatibleType(t1.getTypeName(), t2.getTypeName(), n2Token, n2Line, n2Pos);
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

  public static TypeID getReturnType(Node statements) {
    if (statements instanceof ReturnAST) {
      return ((ReturnAST) statements).getExpr().getIdentifier().getType();
    } else if (statements instanceof ExitAST) {
      return statements.getIdentifier().getType();
    } else if (statements instanceof SequenceAST) {
      List<Node> statsList = ((SequenceAST) statements).getStatements();
      return getReturnType(statsList.get(1));
    } else if (statements instanceof IfAST) {
      Node thenStat = ((IfAST) statements).getThenStat();
      Node elseStat = ((IfAST) statements).getElseStat();
      TypeID thenID = getReturnType(thenStat);
      TypeID elseID = getReturnType(elseStat);
      if (thenID == elseID) {
        return thenID;
      } else {
        SemanticErrorCollector
            .addError("Then return type and else return type is not the same!");
        return null;
      }
    } else if (statements instanceof WhileAST) {
      return getReturnType(((WhileAST) statements).getStat());
    } else {
      SemanticErrorCollector.addError("Missing return statement!");
      return null;
    }
  }
}
