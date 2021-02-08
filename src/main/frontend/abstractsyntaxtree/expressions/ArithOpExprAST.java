package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser;
import antlr.WaccParser.ArithmeticOper1Context;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.IntID;
import frontend.symboltable.SymbolTable;

public class ArithOpExprAST extends Node {

  private String operation;
  private final Node eL;
  private final Node eR;

  public ArithOpExprAST(SymbolTable symtab, String operation,
      Node eL, Node eR) {
    super(symtab.lookupAll("int"));
    this.eL = eL;
    this.eR = eR;
  }

  @Override
  public void check() {
    if (!(eL.getIdentifier().getType() instanceof IntID &&
        eR.getIdentifier().getType() instanceof IntID)) {
      //TODO: Fail
      System.err.println("ArithOp : Incompatible types");
    }
  }
}
