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
  //TODO: Constructors are a mess

  // NOTE that for LDR: destination is lhs and source is rhs
  public LDR(int bytes, String conditions, String dest, String src, int offset) {
    this.bytes = bytes;
    this.conditions = conditions;
    this.src = src;
    this.dest = dest;
    this.offset = offset;
    this.noOffset = offset == 0;
    this.isValue = false;
  }

  // LDR without offset, isValue determines if src is a value
  public LDR(int bytes, String conditions, String dest, String value) {
    this(bytes, conditions, dest, value, 0);
    this.isValue = true;
  }

  // default bytes with offset
  public LDR(String dest, String src, int offset) {
    this(4, "", dest, src, offset);
  }

  // default bytes for value
  public LDR(String dest, String value) {
    this(4, "", dest, value);
    this.isValue = true;
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
      return "=" + src;
    }
    if (noOffset) {
      return "[" + src + "]";
    }
    return "[" + src + ", #" + offset + "]";
  }

  @Override
  public String translateToArm() {
    return getLdr() + " " + dest + ", " + getSrc();
  }
}
