package backend.instructions;

enum AddrModeType {
  REG,
  VAL,
  IMM,
  LOGIC_SHIFT_L,
  LOGIC_SHIFT_R,
  ARITH_SHIFT_L,
  ARITH_SHIFT_R,
  ADDR_OFFSET,
  ADDR_OFFSET_WRITEBACK
}
