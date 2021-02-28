package backend.instructions;

public abstract class Instr {

  public static final String R0 = "r0";
  public static final String R1 = "r1";
  public static final String R2 = "r2";
  public static final String R3 = "r3";
  public static final String R4 = "r4";
  public static final String R5 = "r5";
  public static final String R6 = "r6";
  public static final String R7 = "r7";
  public static final String R8 = "r8";
  public static final String R9 = "r9";
  public static final String R10 = "r10";
  public static final String R11 = "r11";
  public static final String SP = "sp";
  public static final String LR = "lr";
  public static final String PC = "pc";

  private static final String[] regs = {R4, R5, R6, R7, R8, R9, R10, R11};
  private static int regsDepth = 0;

  public static String getTargetReg() {
    return regs[regsDepth];
  }

  public static String incDepth() {
    assert (regsDepth < regs.length - 1);
    return regs[++regsDepth];
  }

  public static String decDepth() {
    assert (regsDepth > 0);
    return regs[--regsDepth];
  }

  public abstract String translateToArm();
}
