package backend.instructions;

enum AddrModeType {
  REG,
  VAL,
  IMM,
  IMM_SHIFT_L,
  IMM_SHIFT_R,
  ADDR_OFFSET,
  ADDR_OFFSET_WRITEBACK
}
