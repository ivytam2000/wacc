package frontend.symboltable;

public class Bool extends Type {

  public static final int MIN = 0;
  public static final int MAX = 1;

  public Bool() {
    super("bool");
  }

  @Override
  public Type getType() {
    return this;
  }
}
