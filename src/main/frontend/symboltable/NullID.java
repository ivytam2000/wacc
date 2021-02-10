package frontend.symboltable;

public class NullID extends PairTypes {

  public NullID() {
    super("null");
  }

  @Override
  public TypeID getType() {
    return this;
  }
}
