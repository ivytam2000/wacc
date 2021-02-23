package backend.instructions;

public class CMP implements Instr {
    private final String reg; // first operand
    private final String operand; // second operand

    public CMP(String reg, String operand) {
        this.reg = reg;
        this.operand = operand;
    }

    @Override public String getInstruction() {
        return "CMP " + reg + ", " + operand;
    }
}
