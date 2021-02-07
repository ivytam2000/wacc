package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.List;

public abstract class AssignRHSAST extends Node {

  private SymbolTable symtab;
  private List<Node> children;

  public AssignRHSAST(Identifier identifier, SymbolTable symtab, List<Node> children) {
    super(identifier);
    this.symtab = symtab;
    this.children = children;
  }

  @Override
  public void check() {
  }
}
