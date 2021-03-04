package backend.instructions;

/**
 * Not implemented in similar fashion since division uses library functions.
 */
public class DIV extends Instr {

  @Override
  public String translateToArm() {
    return "BL __aeabi_idiv";
  }
}
