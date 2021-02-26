package backend.instructions;

public class ORR implements Instr {

  private final String dest; // Also first operand
  private final String operand2;

  public ORR(String dest, String operand2) {
    this.dest = dest;
    this.operand2 = operand2;
  }

  // ORR dest, dest, operand
  @Override
  public String translateToArm() {
    return ("ORR " + dest + ", " + dest + ", " + operand2);
  }
}
