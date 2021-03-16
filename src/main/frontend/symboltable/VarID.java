package frontend.symboltable;

public class VarID extends TypeID {

  public VarID() {
    super("Dynamic Variable");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
