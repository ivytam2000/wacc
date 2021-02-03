package frontend.symboltable;

public class Variable extends Identifier {

  private Type assignedType;
  private String name;

  public Variable(Type assignedType, String name) {
    this.assignedType = assignedType;
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public Type getType() {
    return this.assignedType;
  }
}
