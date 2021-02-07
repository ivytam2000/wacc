package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.symboltable.*;

public class ReadAST extends Node {

  private AssignLHSAST lhs;
  private SymbolTable symtab;

  public ReadAST(SymbolTable symtab, AssignLHSAST assignLHS) {
    this.lhs = assignLHS;
    this.symtab = symtab;
  }

  @Override
  public void check() {
    // Find the 'basic type'
    Identifier lhsType = symtab.lookupAll(lhs.getIdentName());
    if (lhsType == null) {
      System.out.println("Unknown type ");
    } else if (!(lhsType instanceof TypeID)) {
      // check if the type is an identifier
      System.out.println(lhs.getIdentifier().getType() + "is not a type");
    } else if (!(lhsType instanceof IntID) && !(lhsType instanceof CharID)) {
      System.out.println("Read can only take int or char types");
    } else {
      setIdentifier(lhsType);
    }
  }
}
