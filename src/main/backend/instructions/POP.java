package backend.instructions;

public class POP implements Instr {
    private final String reg;

    public POP(String reg) {
        this.reg = reg;
    }

    @Override public String getInstruction() {
        return "PUSH {" + reg + "}";
    }
}
