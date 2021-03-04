package backend.instructions;

public class ADD extends Instr {

  private final boolean setFlags;
  private final String dest; // Also first operand
  private final String src;
  private AddrMode operand;
  private AddrMode shift;

  public ADD(boolean setFlags, String dest, String src, AddrMode operand) {
    this.setFlags = setFlags;
    this.dest = dest;
    this.src = src;
    this.operand = operand;
    this.shift = null;
  }

  public ADD(boolean setFlags, String dest, String src, AddrMode operand,
      AddrMode shift) {
    this.setFlags = setFlags;
    this.dest = dest;
    this.src = src;
    this.operand = operand;
    this.shift = shift;
  }

  /**
   * Returns ADD{S} dest, dest, operand2.
   */
  @Override
  public String translateToArm() {
    String s = "";
    if (shift != null) {
      s = ", " + shift.translateToArm();
    }
    return "ADD" + (setFlags ? "S " : " ")
        + dest + ", " + src + ", " + operand.translateToArm() + s;
  }
}
