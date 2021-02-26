package backend.instructions;

public class STR implements Instr {

  // Number of bytes determines the type of str instruction
  // 4 bytes = word
  private final int bytes;
  private final String conditions;
  private final String src;
  private final String dest;
  private int offset;
  private boolean noOffset;

  // NOTE that for STR: source is lhs and destination is rhs
  public STR(int bytes, String conditions, String src, String dest) {
    this.bytes = bytes;
    this.conditions = conditions;
    this.src = src;
    this.dest = dest;
    this.noOffset = true;
  }

  public STR(int bytes, String conditions, String src, String dest,
      int offset) {
    this(bytes, conditions, src, dest);
    this.offset = offset;
    this.noOffset = false;
  }

  private String getStr() {
    String str = "STR" + conditions;
    switch (bytes) {
      //Byte
      case 1:
        return str + "B";
      //Halfword
      case 2:
        return str + "H";
      //Doubleword
      case 8:
        return str + "D";
      //Word
      default:
        return str;
    }
  }

  private String getDest() {
    if (noOffset) {
      return " [" + dest + "]";
    }
    return " [" + dest + ", " + offset + "]";
  }

  @Override
  public String translateToArm() {
    return getStr() + " " + src + getDest();
  }
}
