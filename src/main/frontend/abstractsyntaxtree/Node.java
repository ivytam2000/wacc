package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

public abstract class Node {

  protected Identifier identifier;
  protected final List<Node> children;

  protected Node(Identifier identifier) {
    this.identifier = identifier;
    this.children = new ArrayList<>();
  }

  protected Node() {
    this(null);
  }

  public Identifier getIdentifier() {
    return identifier;
  }

  public void setIdentifier(Identifier identifier) {
    this.identifier = identifier;
  }

  public abstract void check();
}
