package frontend.symboltable;

public class EmptyID extends TypeID {

  public EmptyID() {
    super("empty");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
