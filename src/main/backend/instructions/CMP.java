package backend.instructions;

public class CMP extends Instr {

  private final String reg; // first operand
  private final AddrMode operand; // second operand
  private final AddrMode shift;

  public CMP(String reg, AddrMode operand, AddrMode shift) {
    this.reg = reg;
    this.operand = operand;
    this.shift = shift;
  }

  public CMP(String reg, AddrMode operand) {
    this.reg = reg;
    this.operand = operand;
    this.shift = null;
  }

  @Override
  public String translateToArm() {
    String instr = "CMP " + reg + ", " + operand.translateToArm();

    if (shift != null) {
      instr += (", " + shift.translateToArm());
    }
    return instr;
  }
}
