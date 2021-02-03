package frontend.symboltable;

public class VariableID extends Identifier {

  private final TypeID assignedType;
  private final String name;

  public VariableID(TypeID assignedType, String name) {
    this.assignedType = assignedType;
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public TypeID getType() {
    return this.assignedType;
  }
}
