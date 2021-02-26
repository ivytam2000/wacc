package backend.instructions;

public class DIV implements Instr {

  //Division uses library functions
  @Override
  public String translateToArm() {
    return "BL __aeabi_idiv";
  }
}
