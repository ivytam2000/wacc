package frontend.symboltable;

public class BoolID extends TypeID {

  public BoolID() {
    super("bool");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
