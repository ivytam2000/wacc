package backend.instructions;

// Very similar to STR
public class LDR implements Instr {

  // Number of bytes determines the type of str instruction
  // 4 bytes = word
  private final int bytes;
  private final String conditions;
  private final String src;
  private final String dest;
  private int offset;
  private boolean noOffset;

  // NOTE that for LDR: destination is lhs and source is rhs
  public LDR(int bytes, String conditions, String dest, String src) {
    this.bytes = bytes;
    this.conditions = conditions;
    this.src = src;
    this.dest = dest;
    this.noOffset = true;
  }

  public LDR(int bytes, String conditions, String dest, String src,
      int offset) {
    this(bytes, conditions, dest, src);
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

  private String getSrc() {
    if (noOffset) {
      return " [" + src + "]";
    }
    return " [" + src + ", " + offset + "]";
  }

  @Override
  public String translateToArm() {
    return getStr() + " " + dest + getSrc();
  }
}
