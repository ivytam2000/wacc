package backend.instructions;

public class SUB extends Instr {

  private final boolean reverse; // For RSB instruction (negation)
  private final boolean setFlags;
  private final String dest; // Also first operand
  private final String operand2;


  public SUB(boolean reverse, boolean setFlags, String dest, String operand2) {
    this.reverse = reverse;
    this.setFlags = setFlags;
    this.dest = dest;
    this.operand2 = operand2;
  }


  // SUB{S} dest, dest, operand2
  // RSBS dest, dest, operand2
  @Override
  public String translateToArm() {
    String base = reverse ? "RSB" : "SUB";
    if (setFlags) {
      base += "S";
    }
    base += " ";
    return (base + dest + ", " + dest + ", " + operand2);
  }
}
