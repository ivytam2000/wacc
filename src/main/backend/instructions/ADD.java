package backend.instructions;

public class ADD extends Instr {

  private final boolean setFlags;
  private final String dest; // Also first operand
  private final String src;
  private final String operand;
  private final String shift;

  public ADD(boolean setFlags, String dest, String src, String operand,
      String shift) {
    this.setFlags = setFlags;
    this.dest = dest;
    this.src = src;
    this.operand = operand;
    this.shift = shift;
  }

  public ADD(boolean setFlags, String dest, String src, String operand) {
    this(setFlags, dest, src, operand, "");
  }

  /**
   * Returns ADD{S} dest, dest, operand2.
   */
  @Override
  public String translateToArm() {
    String s = "";
    if (!shift.equals("")) {
      s = ", " + shift;
    }
    return "ADD" + (setFlags ? "S " : " ")
        + dest + ", " + src + ", " + operand + s;
  }
}
