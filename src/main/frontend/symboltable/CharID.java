package frontend.symboltable;

public class CharID extends TypeID {

  public static final int MIN = 0;
  public static final int MAX = 255;

  public CharID() {
    super("char");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
