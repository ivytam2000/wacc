package frontend.symboltable;

public class VarID extends TypeID {

  private int size;
  private TypeID typeSoFar;

  public VarID(int size) {
    super("Dynamic Variable");
    this.size = size;
  }

  @Override
  public TypeID getType() {
    return this;
  }

  @Override
  public int getBytes() {
    return size;
  }

  public TypeID getTypeSoFar() {
    return typeSoFar;
  }

  public void setTypeSoFar(TypeID typeSoFar) {
    this.typeSoFar = typeSoFar;
  }
}
