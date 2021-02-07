package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class ArgListAST extends Node {

  private List<Node> expressions;
  private SymbolTable symtab;

  public ArgListAST(SymbolTable symtab, List<Node> expressions) {
    this.symtab = symtab;
    this.expressions = expressions;
  }

  @Override
  public void check() {

  }
}
