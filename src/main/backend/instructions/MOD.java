package backend.instructions;

public class MOD implements Instr {

  //Modular arithmetic uses library functions
  @Override
  public String getInstruction() {
    return "BL __aeabi_idivmod";
  }
}
