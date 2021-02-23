package frontend.abstractsyntaxtree.statements;

import antlr.WaccParser.AssignLHSContext;
import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.assignments.AssignLHSAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;
import java.util.List;

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
          "int or char", lhsType.getType().getTypeName(), ctx.getText(),
          line, pos);
    }
  }

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
