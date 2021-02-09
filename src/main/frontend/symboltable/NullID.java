package frontend.symboltable;

public class NullID extends TypeID {

  public NullID() {
    super("null");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
