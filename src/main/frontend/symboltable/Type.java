package frontend.symboltable;

public abstract class Type extends Identifier {

  private String typeName;

  public Type(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return this.typeName;
  }
}
