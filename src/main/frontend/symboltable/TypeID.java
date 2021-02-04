package frontend.symboltable;

public abstract class TypeID extends Identifier {

  private final String typeName;

  protected TypeID(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return this.typeName;
  }
}
