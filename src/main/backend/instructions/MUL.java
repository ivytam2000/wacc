package backend.instructions;

public class MUL extends Instr {

  private final String regLow;  // Also first operand
  private final String regHigh; // Also second operand

  public MUL(String regLow, String regHigh) {
    this.regLow = regLow;
    this.regHigh = regHigh;
  }

  // SMULL regLow, regHigh, regLow, regHigh
  @Override
  public String translateToArm() {
    return "SMULL " + regLow + ", " + regHigh + ", " + regLow + ", " + regHigh;
  }
}
