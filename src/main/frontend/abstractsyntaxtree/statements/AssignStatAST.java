package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.Identifier;
import frontend.symboltable.PairID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class AssignStatAST extends Node {

  private AssignRHSAST rhs;
  private AssignLHSAST lhs;
  private SymbolTable symtab;
  private Identifier assignObj;

  public AssignStatAST(AssignLHSAST lhs, AssignRHSAST rhs, SymbolTable symtab) {
    this.rhs = rhs;
    this.lhs = lhs;
    this.symtab = symtab;
  }

  @Override
  public void check() {
    TypeID t1 = lhs.getIdentifier().getType();
    TypeID t2 = rhs.getIdentifier().getType();
    typeCompat(t1, t2);
  }

  static boolean typeCompat(TypeID t1, TypeID t2) {
    if (!(t1.getTypeName().equals((t2.getTypeName())))) {
      SemanticErrorCollector.addError("LHS and RHS type are not compatible");
      return false;
    }

    if (t1 instanceof PairID) {
      if (((PairID) t1).getFirstType() == ((PairID) t2).getFirstType()) {
        if (((PairID) t1).getSecondType() != ((PairID) t2).getSecondType()) {
          SemanticErrorCollector.addError("Second pair parameter is not the same "
            + "type! Got type "
              + ((PairID) t2).getSecondType().getTypeName()
              + "instead of "
              + ((PairID) t1).getSecondType().getTypeName());
          return false;
        }
        return true;
      }
      SemanticErrorCollector.addError(
          "First pair parameter is not the same type! Got type "
              + ((PairID) t2).getSecondType().getTypeName()
              + "instead of "
              + ((PairID) t1).getSecondType().getTypeName());
      return false;
    }

    if (t1 instanceof ArrayID) {
      if (((ArrayID) t1).getElemType() != ((ArrayID) t2).getElemType()) {
        SemanticErrorCollector.addError(
            "Array elem is not the same type! Got type "
                + ((ArrayID) t2).getElemType().getTypeName()
                + "instead of "
                + ((ArrayID) t1).getElemType().getTypeName());
        return false;
      }
    }
    return true;
  }
}
