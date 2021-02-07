package frontend.abstractsyntaxtree.assignments;

import frontend.abstractsyntaxtree.Node;
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
    lhs.check();
    rhs.check();

    TypeID t1 = lhs.getIdentifier().getType();
    TypeID t2 = rhs.getIdentifier().getType();

    if (t1.getTypeName().equals(t2.getTypeName())) {
      if (t1 instanceof PairID) {
        if (((PairID) t1).getFirstType() == ((PairID) t2).getFirstType()) {
          if (((PairID) t1).getSecondType() != ((PairID) t2).getSecondType()) {
            System.out.println("Second pair parameter is not the same type! Got type " +
                ((PairID) t2).getSecondType().getTypeName() + "instead of " + ((PairID) t1).getSecondType().getTypeName());
          }
        } else {
          System.out.println("First pair parameter is not the same type! Got type " +
              ((PairID) t2).getSecondType().getTypeName() + "instead of " + ((PairID) t1).getSecondType().getTypeName());
        }
      } else if (t1 instanceof ArrayID) {
        if (((ArrayID) t1).getElemType() != ((ArrayID) t2).getElemType()) {
          System.out.println("Array elem is not the same type! Got type " +
              ((ArrayID) t2).getElemType().getTypeName() + "instead of " + ((ArrayID) t1).getElemType().getTypeName());
        }
      } else {
        System.out.println("Something went wrong in AssignAST!");
      }
    }
  }
}