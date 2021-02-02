package frontend.symboltable;

public class Int extends Type {

  public static final int MIN = Integer.MIN_VALUE;
  public static final int MAX = Integer.MAX_VALUE;

  public Int() {
    super("int");
  }

  @Override
  public Type getType() {
    return this;
  }
}
