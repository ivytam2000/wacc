package backend.instructions;

public class MUL implements Instr {

  private final String regLow; //Also first operand
  private final String regHigh; //Also second operand

  public MUL(String regLow, String regHigh) {
    this.regLow = regLow;
    this.regHigh = regHigh;
  }

  //SMULL regLow, regHigh, regLow, regHigh
  @Override
  public String getInstruction() {
    return "SMULL " + regLow + ", " + regHigh + ", " + regLow + ", " + regHigh;
  }
}
