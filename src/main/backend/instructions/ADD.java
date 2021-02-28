package backend.instructions;

public class ADD extends Instr {

  private final boolean setFlags;
  private final String dest; //Also first operand
  private final String src;
  private final String operand;

  public ADD(boolean setFlags, String dest, String src, String operand) {
    this.setFlags = setFlags;
    this.dest = dest;
    this.src = src;
    this.operand = operand;
  }


  // ADD{S} dest, dest, operand2
  @Override
  public String translateToArm() {
    return "ADD" + (setFlags ? "S " : " ")
        + dest + ", " + src + ", " + operand;
   /* return "ADD" + (setFlags ? "S " : " ")
        + dest + ", " + dest + ", " + operand2;*/
  }
}
