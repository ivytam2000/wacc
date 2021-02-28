package backend.instructions;

// Very similar to STR
public class LDR extends Instr {

  // Number of bytes determines the type of str instruction
  // 4 bytes = word
  private final int bytes;
  private final String conditions;
  private final String src;
  private final String dest;
  private boolean isValue;
  private int offset;
  private boolean noOffset;

  //TODO: Signed bytes?

  // NOTE that for LDR: destination is lhs and source is rhs
  public LDR(int bytes, String conditions, String dest, String src) {
    this.bytes = bytes;
    this.conditions = conditions;
    this.src = src;
    this.dest = dest;
    this.noOffset = true;
    this.isValue = false;
  }

  public LDR(int bytes, String conditions, String dest, String src,
      int offset) {
    this(bytes, conditions, dest, src);
    this.offset = offset;
    this.noOffset = false;
    this.isValue = false;
  }

  public LDR(int bytes, String conditions, String dest, String src,
      int offset, boolean isValue) {
    this(bytes, conditions, dest, src);
    this.offset = offset;
    this.noOffset = false;
    this.isValue = isValue;
  }

  public LDR(String dest, String src, int offset) {
    this(0, "", dest, src);
    this.offset = offset;
    this.noOffset = false;
    this.isValue = true;
  }

  public LDR(String dest, String src, boolean isValue) {
    this(0, "", dest, src);
    this.offset = 0;
    this.noOffset = false;
    this.isValue = isValue;
  }

  //TODO: Fix switch case
  private String getLdr() {
    String ldr = "LDR";
    switch (bytes) {
      //Signed byte
      case 1:
        ldr += "SB";
        break;
      //Signed halfword
      case -2:
        ldr += "SH";
        break;
      //Halfword
      case 2:
        ldr += "H";
        break;
      //Word
      default:
    }
    return ldr + conditions;
  }

  private String getSrc() {
    if (isValue) {
      return src;
    }
    if (noOffset) {
      return " [" + src + "]";
    }
    return " [" + src + ", #" + offset + "]";
  }

  @Override
  public String translateToArm() {
    return getLdr() + " " + dest + getSrc();
  }
}
