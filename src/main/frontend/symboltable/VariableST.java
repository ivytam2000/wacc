package frontend.symboltable;

public class VariableST extends IdentifierST {

  private final TypeST assignedType;
  private final String name;

  public VariableST(TypeST assignedType, String name) {
    this.assignedType = assignedType;
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public TypeST getType() {
    return this.assignedType;
  }
}
