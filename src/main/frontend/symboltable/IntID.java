package frontend.symboltable;

public class IntID extends TypeID {

  public static final Integer MIN = Integer.MIN_VALUE;
  public static final Integer MAX = Integer.MAX_VALUE;

  public IntID() {
    super("int");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
