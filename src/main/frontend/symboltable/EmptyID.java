package frontend.symboltable;

public class EmptyID extends TypeID {

  // Used for empty arrays
  public EmptyID() {
    super("empty");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
