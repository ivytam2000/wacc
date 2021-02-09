package frontend.abstractsyntaxtree.statements;

import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.SymbolTable;

public class ReturnAST extends Node {
  private final SymbolTable symtab;
  private final Node expr;

  public ReturnAST(SymbolTable symtab, Node expr) {
    super(expr.getIdentifier());
    this.symtab = symtab;
    this.expr = expr;
  }

  public Node getExpr() {
    return expr;
  }

  @Override
  public void check() {
    if (symtab.isTopLevel()) {
      SemanticErrorCollector.addError("Cannot return in main program!");
    }
  }
}