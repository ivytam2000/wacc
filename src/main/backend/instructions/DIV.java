package backend.instructions;

public class DIV implements Instr {

  //Division uses library functions
  @Override
  public String getInstruction() {
    return "BL __aeabi_idiv";
  }
}
