package frontend.symboltable;

public class CharID extends TypeID {

  public CharID() {
    super("char");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
