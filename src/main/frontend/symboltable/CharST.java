package frontend.symboltable;

public class CharST extends TypeST {

  public static final int MIN = 0;
  public static final int MAX = 255;

  public CharST() {
    super("char");
  }

  @Override
  public TypeST getType() {
    return this;
  }
}
