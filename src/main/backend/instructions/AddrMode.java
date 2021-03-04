package backend.instructions;

public class AddrMode extends Instr {

  private final Object operand;
  private final int offsetFromOperand;
  private final AddrModeType type;

  public AddrMode(Object operand, int offsetFromOperand, AddrModeType type) {
    this.operand = operand;
    this.offsetFromOperand = offsetFromOperand;
    this.type = type;
  }

  @Override
  public String translateToArm() {
    switch (type) {
      case VAL:
        return "=" + operand;
      case IMM:
        return "#" + operand;
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
