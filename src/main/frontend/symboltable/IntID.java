package frontend.symboltable;

public class IntID extends TypeID {

  public static final int MIN = Integer.MIN_VALUE;
  public static final int MAX = Integer.MAX_VALUE;

  public IntID() {
    super("int");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
