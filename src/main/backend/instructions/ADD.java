package backend.instructions;

public class ADD implements Instr {

  private final boolean setFlags;
  private final String dest; //Also first operand
  private final String operand2;

  public ADD(boolean setFlags, String dest, String operand2) {
    this.setFlags = setFlags;
    this.dest = dest;
    this.operand2 = operand2;
  }

  // ADD{S} dest, dest, operand2
  @Override
  public String translateToArm() {
    return "ADD" + (setFlags ? "S " : " ")
        + dest + ", " + dest + ", " + operand2;
  }
}
