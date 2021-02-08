package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.symboltable.*;

public class ReadAST extends Node {

  private final AssignLHSAST lhs;

  public ReadAST(AssignLHSAST assignLHS) {
    this.lhs = assignLHS;
  }

  @Override
  public void check() {
    Identifier lhsType = lhs.getIdentifier();

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
