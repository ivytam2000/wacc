package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser.UnaryOperContext;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ArrayID;
import frontend.symboltable.BoolID;
import frontend.symboltable.CharID;
import frontend.symboltable.Identifier;
import frontend.symboltable.IntID;
import frontend.symboltable.StringID;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class UnOpExprAST extends Node {

  private final SymbolTable symbtab;
  private final UnaryOperContext ctx;
  private final Node exprAST;

  public UnOpExprAST(SymbolTable symbtab, Node exprAST, UnaryOperContext ctx) {
    super();
    this.symbtab = symbtab;
    this.ctx = ctx;
    this.exprAST = exprAST;
  }

  @Override
  public void check() {
    TypeID exprType = exprAST.getIdentifier().getType();
    Identifier unOpExprType = null;
    boolean error = false;
    if (ctx.NOT() != null) {
      if (!(exprType instanceof BoolID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("bool");
    } else if (ctx.MINUS() != null) {
      if (!(exprType instanceof IntID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("int");
    } else if (ctx.CHR() != null) {
      if (!(exprType instanceof IntID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("char");
    } else if (ctx.LEN() != null) {
      if (!(exprType instanceof StringID || exprType instanceof ArrayID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("int");
    } else if (ctx.ORD() != null) {
      if (!(exprType instanceof CharID)) {
        error = true;
      }
      unOpExprType = symbtab.lookupAll("int");
    }
    if (error) {
      SemanticErrorCollector
          .addIncompatibleType(unOpExprType.getType().getTypeName(),
              exprType.getTypeName(),
              ctx.getText(), ctx.getStart().getLine(),
              ctx.getStart().getCharPositionInLine());
    }
    setIdentifier(unOpExprType);
  }
}
