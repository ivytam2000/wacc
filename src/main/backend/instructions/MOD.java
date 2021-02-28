package backend.instructions;

public class MOD extends Instr {

  // Modular arithmetic uses library functions
  @Override
  public String translateToArm() {
    return "BL __aeabi_idivmod";
  }
}
