package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public abstract class Node {

  protected Identifier identifier;
  protected final SymbolTable symtab;
  protected final Node parent;
  protected final List<Node> children;

  protected Node(Identifier identifier, SymbolTable symtab, Node parent) {
    this.identifier = identifier;
    this.symtab = symtab;
    this.parent = parent;
    this.children = new ArrayList<>();
  }

  protected Node(SymbolTable symtab, Node parent) {
    this(null, symtab, parent);
  }

  public Identifier getIdentifier() {
    return identifier;
  }

  public void setIdentifier(Identifier identifier) {
    this.identifier = identifier;
  }

  public abstract void check();
}
