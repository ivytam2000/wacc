package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser;
import antlr.WaccParser.UnaryOperContext;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
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

  public UnOpExprAST(SymbolTable symbtab, UnaryOperContext ctx, Node exprAST) {
    super();
    this.symbtab = symbtab;
    this.ctx = ctx;
    this.exprAST = exprAST;
  }

  @Override
  public void check() {
    TypeID exprType = exprAST.getIdentifier().getType();
    Identifier unOpExprType = null;
    if (ctx.NOT() != null) {
      if (exprType instanceof BoolID) {
        unOpExprType = symbtab.lookupAll("bool");
      }
    } else if (ctx.MINUS() != null || ctx.CHR() != null) {
      if (exprType instanceof IntID) {
        unOpExprType = symbtab.lookupAll("int");
      }
    } else if (ctx.LEN() != null) {
      if (exprType instanceof CharID) {
        unOpExprType = symbtab.lookupAll("char");
      } else if (exprType instanceof StringID) {
        unOpExprType = symbtab.lookupAll("string");
      }
    } else if (ctx.ORD() != null) {
      if (exprType instanceof CharID) {
        unOpExprType = symbtab.lookupAll("char");
      }
    }
    if (identifier == null) {
      SemanticErrorCollector.addError("Unary Operator : Incompatible type");
    } else {
      setIdentifier(unOpExprType);
    }
  }
}
