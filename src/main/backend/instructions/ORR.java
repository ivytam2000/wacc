package backend.instructions;

public class ORR extends Instr {

  private final boolean exclusive;
  private final String dest; // Also first operand
  private final String operand2;

  public ORR(boolean exclusive, String dest, String operand2) {
    this.exclusive = exclusive;
    this.dest = dest;
    this.operand2 = operand2;
  }

  // ORR dest, dest, operand
  @Override
  public String translateToArm() {
    String or = exclusive ? "EOR " : "ORR ";
    return (or + dest + ", " + dest + ", " + operand2);
  }
}
