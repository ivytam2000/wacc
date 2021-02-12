package frontend.abstractsyntaxtree;

import frontend.abstractsyntaxtree.statements.BeginStatAST;
import frontend.abstractsyntaxtree.statements.ExitAST;
import frontend.abstractsyntaxtree.statements.IfAST;
import frontend.abstractsyntaxtree.statements.ReturnAST;
import frontend.abstractsyntaxtree.statements.SequenceAST;
import frontend.abstractsyntaxtree.statements.WhileAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.EmptyID;
import frontend.symboltable.ExitID;
import frontend.symboltable.NullID;
import frontend.symboltable.PairID;
import frontend.symboltable.OptionalPairID;
import frontend.symboltable.TypeID;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Pair;

public class Utils {

  public static final int BOOL = 1;
  public static final int INT_CHAR = 2;
  public static final int ALL_TYPES = 3;

  public static boolean typeCompat(TypeID t1, TypeID t2) {
    assert (t1 != null);
    assert (t2 != null);

    // For function return type comparison
    if (t1 instanceof NullID) {
      return t2 instanceof OptionalPairID;
    }

    // Check pair types
    if (t1 instanceof PairID) {
      if (t2 instanceof OptionalPairID) {
        return comparePairTypes((PairID) t1, (OptionalPairID) t2);
      }
      return false;
    }

    // Check array types
    if (t1 instanceof ArrayID) {
      if (t2 instanceof ArrayID) {
        return compareArrayTypes(t1, t2);
      }
      return false;
    }

    // Check base types
    return t1.getType() == t2.getType();
  }

  public static boolean comparePairTypes(PairID eLType, OptionalPairID eRType) {
    // Assigning null to pair
    if (eRType instanceof NullID) {
      return true;
    }

    // Types of LHS pair
    TypeID fstLType = eLType.getFstType();
    TypeID sndLType = eLType.getSndType();

    // Types of RHS pair
    TypeID fstRType = ((PairID) eRType).getFstType();
    TypeID sndRType = ((PairID) eRType).getSndType();

    // Do shallow comparison between types within pairs

    // Both pairs does not contain nulls
    if (!(fstLType instanceof NullID)
        && !(sndLType instanceof NullID)
        && !(fstRType instanceof NullID)
        && !(sndRType instanceof NullID)) {
      return (fstLType.getClass() == fstRType.getClass())
          && (sndLType.getClass() == sndRType.getClass());
    }

    // first of each pair is not null
    if (!(fstLType instanceof NullID) && !(fstRType instanceof NullID)) {
      return (fstLType.getClass() == fstRType.getClass());
    }

    // second of each pair is not null
    if (!(sndRType instanceof NullID) && !(sndLType instanceof NullID)) {
      return (sndLType.getClass() == sndRType.getClass());
    }

    return true;
  }

  public static boolean compareArrayTypes(TypeID eLType, TypeID eRType) {
    // Can always assign empty to any array
    if (eRType instanceof EmptyID) {
      return true;
    }

    // Recurse to find underlying type
    if (eLType instanceof ArrayID && eRType instanceof ArrayID) {
      return compareArrayTypes(((ArrayID) eLType).getElemType(), ((ArrayID) eRType).getElemType());
    }

    // Call comparePairTypes is underlying type is pair
    if (eLType instanceof PairID && eRType instanceof OptionalPairID) {
      return comparePairTypes((PairID) eLType, (OptionalPairID) eRType);
    }

    // Check base types
    return typeCompat(eLType, eRType);
  }

// TODO : Turn into a switch statment??
  public static TypeID inferFinalReturnType(Node statements, int line) {
    if (statements instanceof ReturnAST) {
      // Base case
      return ((ReturnAST) statements).getExpr().getIdentifier().getType();
    } else if (statements instanceof ExitAST) {
      // Base case
      return statements.getIdentifier().getType();
    } else if (statements instanceof SequenceAST) {
      // Assume the final type of a sequence is found in the last statement
      List<Node> statsList = ((SequenceAST) statements).getStatements();
      return inferFinalReturnType(statsList.get(1), line);
    } else if (statements instanceof IfAST) {
      // Need to check both branch of execution for IfAST
      Node thenStat = ((IfAST) statements).getThenStat();
      Node elseStat = ((IfAST) statements).getElseStat();
      TypeID thenID = inferFinalReturnType(thenStat, line);
      TypeID elseID = inferFinalReturnType(elseStat, line);
      if (!(thenID instanceof ExitID || elseID instanceof ExitID || typeCompat(thenID, elseID))) {
        // Type of an if-statement should be same regardless which statements
        SemanticErrorCollector.addIfReturnTypesError(line);
      }
      return thenID instanceof ExitID ? elseID : thenID;
    } else if (statements instanceof WhileAST) {
      // Assume the final type of a while-block is found within the block
      return inferFinalReturnType(((WhileAST) statements).getStat(), line);
    } else if (statements instanceof BeginStatAST) {
      return inferFinalReturnType(((BeginStatAST) statements).getStat(), line);
    }
    // UNREACHABLE (Parser makes sure that there is always return/exit)
    return null;
  }
}
