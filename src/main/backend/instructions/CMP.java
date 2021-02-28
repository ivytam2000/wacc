package backend.instructions;

public class CMP extends Instr {

  private final String reg; // first operand
  private final String operand; // second operand
  private final String shift;

  public CMP(String reg, String operand, String shift) {
    this.reg = reg;
    this.operand = operand;
    this.shift = shift;
  }

  public CMP(String reg, String operand) {
    this(reg, operand, "");
  }

  @Override
  public String translateToArm() {
    String instr = "CMP " + reg + ", " + operand;
    if (shift != null) {
      instr += (", " + shift);
    }
    return instr;
  }
}
