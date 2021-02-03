package frontend.symboltable;

public abstract class TypeST extends IdentifierST {

  private final String typeName;

  public TypeST(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return this.typeName;
  }
}
