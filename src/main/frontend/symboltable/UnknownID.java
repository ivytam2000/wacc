package frontend.symboltable;

public class UnknownID extends TypeID {

  public UnknownID() {
    super("unknown");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
