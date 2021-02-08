package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

public class ReadAST extends Node {

  private final AssignLHSAST lhs;

  public ReadAST(AssignLHSAST assignLHS) {
    super(assignLHS.getIdentifier());
    this.lhs = assignLHS;
  }

  @Override
  public void check() {
    Identifier lhsType = lhs.getIdentifier();

    if (lhsType == null) {
      SemanticErrorCollector.addError("Unknown type");
    } else if (!(lhsType instanceof TypeID)) {
      // check if the type is an identifier
      SemanticErrorCollector.addError(lhs.getIdentifier().getType() + "is not a type");
    } else if (!(lhsType instanceof IntID) && !(lhsType instanceof CharID)) {
      SemanticErrorCollector.addError("Read can only take int or char types");
    } else {
      setIdentifier(lhsType);
    }
  }
}
