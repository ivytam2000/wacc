package frontend.abstractsyntaxtree.assignments;

import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.List;

public abstract class AssignRHSAST extends Node {

  protected SymbolTable symtab;
  protected List<Node> children;

  public AssignRHSAST(Identifier identifier, SymbolTable symtab, List<Node> children) {
    super(identifier);
    this.symtab = symtab;
    this.children = children;
  }

  public AssignRHSAST(SymbolTable symtab, List<Node> children) {
    this.symtab = symtab;
    this.children = children;
  }
}