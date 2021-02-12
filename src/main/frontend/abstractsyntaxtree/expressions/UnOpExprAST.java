package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser;
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
    String expectedType = null;
    if (ctx.NOT() != null) {
      expectedType = "bool";
      if (exprType instanceof BoolID) {
        unOpExprType = symbtab.lookupAll("bool");
      }
    } else if (ctx.MINUS() != null) {
      expectedType = "int";
      if (exprType instanceof IntID) {
        unOpExprType = symbtab.lookupAll("int");
      }
    } else if (ctx.CHR() != null) {
      expectedType = "char";
      if (exprType instanceof IntID) {
        unOpExprType = symbtab.lookupAll("char");
      }
    } else if (ctx.LEN() != null) {
      expectedType = "int";
      if (exprType instanceof StringID || exprType instanceof ArrayID) {
        unOpExprType = symbtab.lookupAll("int");
      }
    } else if (ctx.ORD() != null) {
      expectedType = "int";
      if (exprType instanceof CharID) {
        unOpExprType = symbtab.lookupAll("int");
      }
    }
    if (unOpExprType == null) {
      SemanticErrorCollector.addIncompatibleType(expectedType, exprType.getTypeName(),
          ctx.getText(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    } else {
      setIdentifier(unOpExprType);
    }
  }
}
