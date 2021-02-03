package frontend.symboltable;

public class IntST extends TypeST {

  public static final int MIN = Integer.MIN_VALUE;
  public static final int MAX = Integer.MAX_VALUE;

  public IntST() {
    super("int");
  }

  @Override
  public TypeST getType() {
    return this;
  }
}
