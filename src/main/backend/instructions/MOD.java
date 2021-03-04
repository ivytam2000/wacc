package backend.instructions;

/**
 * Not implemented in similar fashion since modular arithmetic uses library
 * functions.
 */
public class MOD extends Instr {

  @Override
  public String translateToArm() {
    return "BL __aeabi_idivmod";
  }
}
