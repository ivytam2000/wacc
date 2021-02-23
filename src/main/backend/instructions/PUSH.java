package backend.instructions;

public class PUSH implements Instr {
    private final String reg;

    public PUSH(String reg) {
        this.reg = reg;
    }

    @Override public String getInstruction() {
        return "PUSH {" + reg + "}";
    }
}
