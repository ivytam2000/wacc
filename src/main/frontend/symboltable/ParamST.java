package frontend.symboltable;

public class ParamST extends IdentifierST {

  private final TypeST type;

  public ParamST(TypeST type) {
    this.type = type;
  }

  @Override
  public TypeST getType() {
    return type;
  }
}
