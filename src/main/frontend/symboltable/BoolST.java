package frontend.symboltable;

public class BoolST extends TypeST {

  public static final int MIN = 0;
  public static final int MAX = 1;

  public BoolST() {
    super("bool");
  }

  @Override
  public TypeST getType() {
    return this;
  }
}
