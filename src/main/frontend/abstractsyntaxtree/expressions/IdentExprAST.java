package frontend.abstractsyntaxtree.expressions;

import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class IdentExprAST extends Node {

  private final SymbolTable currsymtab;
  private final String val;

  public IdentExprAST(SymbolTable currsymtab, String val) {
    super();
    this.currsymtab = currsymtab;
    this.val = val;
  }

  public String getName() {
    return val;
  }

  @Override
  public void check() {
    //TODO: Check that this is correct after variables are added to symtab
    Identifier identifier = currsymtab.lookupAll(val);
    if (identifier == null) {
      //Unknown variable
      SemanticErrorCollector.addError(val + " is unknown");
    } else {
      setIdentifier(identifier);
    }
  }
}
