package backend.instructions;

public interface Instr {

  static final String R0  = "r0";
  static final String R1  = "r1";
  static final String R2  = "r2";
  static final String R3  = "r3";
  static final String R4  = "r4";
  static final String R5  = "r5";
  static final String R6  = "r6";
  static final String R7  = "r7";
  static final String R8  = "r8";
  static final String R9  = "r9";
  static final String R10 = "r10";
  static final String R11 = "r11";
  static final String SP  = "sp";
  static final String LR  = "lr";
  static final String PC  = "pc";

  String getInstruction();

}
