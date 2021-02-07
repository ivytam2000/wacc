package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.VariableID;

public class IdentExprAST extends Node {

  private final SymbolTable currsymtab;
  private final String val;

  public IdentExprAST(SymbolTable currsymtab, String val) {
    super();
    this.currsymtab = currsymtab;
    this.val = val;
  }

  @Override
  public void check() {
    Identifier identifier = currsymtab.lookupAll(val);
    if (identifier == null) {
      //Unknown variable
      System.err.println("Unknown variable");
    } else if (identifier instanceof VariableID) {
      System.err.println(val + " is not a variable");
    } else {
      setIdentifier(identifier);
    }
  }
}
