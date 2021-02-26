package backend.instructions;

public class MOD implements Instr {

  // Modular arithmetic uses library functions
  @Override
  public String translateToArm() {
    return "BL __aeabi_idivmod";
  }
}
