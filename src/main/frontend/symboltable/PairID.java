package frontend.symboltable;

public class PairID extends OptionalPairID {

  private TypeID fst;
  private TypeID snd;

  public TypeID getFstType() {
    return fst;
  }

  public TypeID getSndType() {
    return snd;
  }

  public PairID() {
    super("pair");
  }

  public PairID(TypeID fst, TypeID snd) {
    super("pair");
    this.fst = fst;
    this.snd = snd;
  }

  @Override
  public TypeID getType() {
    return this;
  }

  @Override
  public String getTypeName() {
    if (fst != null && snd != null) {
      return String.format("pair(%s,%s)", fst.getTypeName(),
          snd.getTypeName());
    }
    return super.getTypeName();
  }
}
