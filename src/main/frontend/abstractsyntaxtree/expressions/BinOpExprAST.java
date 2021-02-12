package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser.ExprContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.BoolID;
import frontend.symboltable.CharID;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class BinOpExprAST extends Node {

  private final String op;
  private final int expectedExprTypes;
  private final Node eL;
  private final Node eR;
  private final ExprContext ctx;

  public BinOpExprAST(SymbolTable symtab, int expectedExprTypes,
      String op, Node eL, Node eR, ExprContext ctx) {
    super(symtab.lookupAll("bool")); //BinOpExpr always has bool return type
    this.op = op;
    this.expectedExprTypes = expectedExprTypes;
    this.eL = eL;
    this.eR = eR;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    TypeID eLType = eL.getIdentifier().getType();
    TypeID eRType = eR.getIdentifier().getType();

    //Different binOps are compatible w different types

    if (expectedExprTypes == Utils.ALL_TYPES) { //Defined for all types
      if (!Utils.typeCompat(eLType, eRType)) {
        SemanticErrorCollector
            .addTypeMismatch(ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(), op);
      }
    } else {
      //Check for left and right expr of binOp separated to pin point error

      boolean errorL = false;
      boolean errorR = false;
      String expectedTypes = "";

      if (expectedExprTypes == Utils.INT_CHAR) { //Defined for int and char
        errorL = !(eLType instanceof IntID || eLType instanceof CharID);
        errorR = !(eRType instanceof IntID || eRType instanceof CharID);
        expectedTypes = "int or char";
      } else if (expectedExprTypes == Utils.BOOL) { //Defined for bool only
        errorL = !(eLType instanceof BoolID);
        errorR = !(eRType instanceof BoolID);
        expectedTypes = "bool";
      }

      if (errorL) {
        SemanticErrorCollector
            .addIncompatibleType(expectedTypes, eLType.getTypeName(), ctx.getText(),
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine());
      }

      if (errorR) {
        SemanticErrorCollector
            .addIncompatibleType(expectedTypes, eRType.getTypeName(), ctx.getText(),
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine());
      }
    }
  }


}
