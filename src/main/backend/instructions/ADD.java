package backend.instructions;

public class ADD implements Instr {

  private final boolean setFlags;
  private final String dest; //Also first operand
  private final String src;
  private final String immediate;

  public ADD(boolean setFlags, String dest, String src, String immediate) {
    this.setFlags = setFlags;
    this.dest = dest;
    this.src = src;
    this.immediate = immediate;

  }


  // ADD{S} dest, dest, operand2
  @Override
  public String translateToArm() {
    return "ADD" + (setFlags ? "S " : " ")
        + dest + ", " + src + ", " + immediate;
   /* return "ADD" + (setFlags ? "S " : " ")
        + dest + ", " + dest + ", " + operand2;*/
  }
}
