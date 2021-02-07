package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;

public abstract class Node {

  protected Identifier identifier;

  protected Node(Identifier identifier) {
    this.identifier = identifier;
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
