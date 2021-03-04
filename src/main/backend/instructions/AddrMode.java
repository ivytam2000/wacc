package backend.instructions;

public class AddrMode extends Instr {

  private final Object operand;
  private final int offsetFromOperand;
  private final AddrModeType type;

  private AddrMode(Object operand, int offsetFromOperand, AddrModeType type) {
    this.operand = operand;
    this.offsetFromOperand = offsetFromOperand;
    this.type = type;
  }

  public AddrMode buildVal(Object operand) {
    return new AddrMode(operand, 0, AddrModeType.VAL);
  }

  public AddrMode buildImm(Object operand) {
    return new AddrMode(operand, 0, AddrModeType.IMM);
  }

  public AddrMode buildImmWithShiftL(Object operand) {
    return new AddrMode(operand, 0, AddrModeType.IMM_SHIFT_L);
  }

  public AddrMode buildImmWithShiftR(Object operand) {
    return new AddrMode(operand, 0, AddrModeType.IMM_SHIFT_R);
  }

  public AddrMode buildAddr(Object operand) {
    return new AddrMode(operand, 0, AddrModeType.ADDR_OFFSET);
  }

  public AddrMode buildAddrWithOffset(Object operand, int offsetFromOperand) {
    return new AddrMode(operand, offsetFromOperand, AddrModeType.ADDR_OFFSET);
  }

  public AddrMode buildAddrWithWriteBack(Object operand, int offsetFromOperand) {
    return new AddrMode(operand, offsetFromOperand, AddrModeType.ADDR_OFFSET_WRITEBACK);
  }

  @Override
  public String translateToArm() {
    switch (type) {
      case VAL:
        return "=" + operand;
      case IMM:
        return "#" + operand;
      case IMM_SHIFT_L:
        return "LSL #" + operand;
      case IMM_SHIFT_R:
        return "LSR #" + operand;
      case ADDR_OFFSET:
        if (offsetFromOperand == 0) {
          return ("[" + operand + "]");
        } else {
          return ("[" + operand + ", #" + offsetFromOperand + "]");
        }
      case ADDR_OFFSET_WRITEBACK:
        return ("[" + operand + ", #" + offsetFromOperand + "]!");
      default:
        return "";
    }
  }
}
