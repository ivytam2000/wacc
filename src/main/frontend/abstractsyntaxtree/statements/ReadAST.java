package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

public class ReadAST extends Node {

  private final AssignLHSAST lhs;
  private WaccParser.AssignLHSContext ctx;

  public ReadAST(AssignLHSAST assignLHS, WaccParser.AssignLHSContext ctx) {
    super(assignLHS.getIdentifier());
    this.lhs = assignLHS;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    Identifier lhsType = lhs.getIdentifier();
    int line = ctx.getStart().getLine();
    int pos =  ctx.getStart().getCharPositionInLine();

    if (lhsType == null) {
      SemanticErrorCollector.addUnknownType(lhs.getIdentName(), line, pos);
    } else if (!(lhsType instanceof TypeID)) {
      String errMsg = String.format("line %d:%d -- %s is not a type",line,
          pos,lhsType.getType().getTypeName());
      SemanticErrorCollector.addError(errMsg);
    } else if (!(lhsType instanceof IntID) && !(lhsType instanceof CharID)) {
      SemanticErrorCollector.addIncompatibleType("int or char",
          lhsType.getType().getTypeName(),ctx.getText(),line, pos);
    } else {
      setIdentifier(lhsType);
    }
  }
}
