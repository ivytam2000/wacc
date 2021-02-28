package frontend.symboltable;

public class StringID extends TypeID {

  // TODO
  String dataSegment;
  // If dataSegment is unassigned that means string is not a variable?

  public StringID() {
    super("string");
  }

  @Override
  public TypeID getType() {
    return this;
  }

  @Override public int getBytes() {return 4;}
}
