package backend.instructions;

// Very similar to STR
public class LDR extends Instr {

  // Number of bytes determines the type of str instruction
  // 4 bytes = word
  private final int bytes;
  private final String conditions;
  private String src;
  private AddrMode addrMode;
  private final String dest;
  private boolean isValue;
  private int offset;
  private boolean noOffset;

  //TODO: Signed bytes?
  //TODO: Constructors are a mess

  public LDR(int bytes, String conditions, String dest, AddrMode addrMode) {
    this.addrMode = addrMode;
    this.bytes = bytes;
    this.conditions = conditions;
    this.dest = dest;
  }

  public LDR(String dest, AddrMode addrMode) {
    this(4, "", dest, addrMode);
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
    return getLdr() + " " + dest + ", " + addrMode.translateToArm();
  }
}
