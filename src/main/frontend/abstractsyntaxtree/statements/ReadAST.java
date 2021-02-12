package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser;
import antlr.WaccParser.AssignLHSContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

public class ReadAST extends Node {

  private final AssignLHSAST lhs;
  private final AssignLHSContext ctx;

  public ReadAST(AssignLHSAST assignLHS, AssignLHSContext ctx) {
    super();
    this.lhs = assignLHS;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    Identifier lhsType = lhs.getIdentifier();
    int line = ctx.getStart().getLine();
    int pos = ctx.getStart().getCharPositionInLine();

    if (!(lhsType instanceof IntID) && !(lhsType instanceof CharID)) {
      SemanticErrorCollector.addIncompatibleType(
          "int or char", lhsType.getType().getTypeName(), ctx.getText(), line, pos);
    }
  }
}

//if (lhsType == null) {
//    SemanticErrorCollector.addUnknownType(lhs.getIdentName(), line, pos);
//    } else

//else if (!(lhsType instanceof TypeID)) {
//    String errMsg =
//    String.format(
//    "line %d:%d -- %s is not a type", line, pos, lhsType.getType().getTypeName());
//    SemanticErrorCollector.addError(errMsg);
//    }
