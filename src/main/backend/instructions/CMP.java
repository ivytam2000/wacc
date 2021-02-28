package backend.instructions;

public class CMP extends Instr {

  private final String reg; // first operand
  private final String operand; // second operand

  public CMP(String reg, String operand) {
    this.reg = reg;
    this.operand = operand;
  }

  @Override
  public String translateToArm() {
    return "CMP " + reg + ", " + operand;
  }
}
