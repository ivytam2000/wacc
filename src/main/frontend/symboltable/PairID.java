package frontend.symboltable;

public class PairID extends TypeID {

  private TypeID first;
  private TypeID second;

  public TypeID getFirstType() {
    return first;
  }

  public TypeID getSecondType() {
    return second;
  }

  public PairID(TypeID first, TypeID second) {
    super("pair");
    this.first = first;
    this.second = second;
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
