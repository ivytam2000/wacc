package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser.ArithOpExpr_1Context;
import antlr.WaccParser.ArithOpExpr_2Context;
import antlr.WaccParser.ExprContext;
import backend.instructions.Instr;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import frontend.symboltable.UnknownID;
import java.util.List;

public class ArithOpExprAST extends Node {

  private final String op;
  private final Node eL;
  private final Node eR;
  private final ExprContext ctx;

  public ArithOpExprAST(SymbolTable symtab, String op, Node eL, Node eR,
      ExprContext ctx) {
    super(symtab.lookupAll("int"));
    this.op = op;
    this.eL = eL;
    this.eR = eR;
    assert (ctx instanceof ArithOpExpr_1Context
        || ctx instanceof ArithOpExpr_2Context);
    this.ctx = ctx;
  }

  @Override
  public void check() {
    int fstStatPosition = 0;
    int sndStatPosition = 2;

    TypeID eLType = eL.getIdentifier().getType();
    TypeID eRType = eR.getIdentifier().getType();

    //Both eL and eR must be of type int to do arithmetic

    if (!(eLType instanceof IntID || eLType instanceof UnknownID)) {
      SemanticErrorCollector
          .addIncompatibleType("int (For " + op + ")", eLType.getTypeName(),
              ctx.children.get(fstStatPosition).getText(),
              ctx.getStart().getLine(),
              ctx.getStart().getCharPositionInLine());
    }

    if (!(eRType instanceof IntID || eRType instanceof UnknownID)) {
      SemanticErrorCollector
          .addIncompatibleType("int (For " + op + ")", eRType.getTypeName(),
              ctx.children.get(sndStatPosition).getText(),
              ctx.getStart().getLine(),
              ctx.getStop().getCharPositionInLine());
    }
  }

  @Override
  public List<Instr> toAssembly() {
    return null;
  }
}
