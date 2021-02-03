package frontend.symboltable;

public class BoolID extends TypeID {

  public static final int MIN = 0;
  public static final int MAX = 1;

  public BoolID() {
    super("bool");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
