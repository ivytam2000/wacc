package frontend.symboltable;

public class Char extends Type {

  public static final int MIN = 0;
  public static final int MAX = 255;

  public Char() {
    super("char");
  }

  @Override
  public Type getType() {
    return this;
  }
}
