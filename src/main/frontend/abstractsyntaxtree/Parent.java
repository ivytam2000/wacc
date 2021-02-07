package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import java.util.ArrayList;
import java.util.List;

public abstract class Parent extends Node {

  protected final List<Node> children = new ArrayList<>();

  protected Parent(Identifier identifier) {
    super(identifier);
  }

  protected Parent() {
    super();
  }

  @Override
  public abstract void check();
}
