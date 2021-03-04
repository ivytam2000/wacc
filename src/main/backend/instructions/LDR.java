package backend.instructions;

// Very similar to STR
public class LDR extends Instr {

  // Number of bytes determines the type of str instruction
  // 4 bytes = word
  private final int bytes;
  private final String conditions;
  private final AddrMode addrMode;
  private final String dest;

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

  @Override
  public String translateToArm() {
    return getLdr() + " " + dest + ", " + addrMode.translateToArm();
  }
}
