package backend.instructions;

enum AddrModeType {
  REG,
  VAL,
  IMM,
  IMM_SHIFT_L,
  IMM_SHIFT_R,
  ARITH_SHIFT_L,
  ADDR_OFFSET,
  ADDR_OFFSET_WRITEBACK
}
