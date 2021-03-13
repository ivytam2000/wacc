package frontend.symboltable;

import frontend.abstractsyntaxtree.classes.Visibility;

public abstract class Identifier {

  // Public by default
  public Visibility visibility = Visibility.PUBLIC;

  public void setVisibility(Visibility visibility) {
    this.visibility = visibility;
  }

  public abstract TypeID getType();
}
