package frontend.abstractsyntaxtree.expressions;

import antlr.WaccParser;
import antlr.WaccParser.ArithmeticOper1Context;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
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
    boolean eLIsInt = eL.getIdentifier().getType() instanceof IntID;
    boolean eRIsInt = eR.getIdentifier().getType() instanceof IntID;

    if (!(eLIsInt && eRIsInt)) {
      SemanticErrorCollector.addError("Arithmetic operator : Incompatible types. Expected INT.");
    }
  }
}
