package backend.instructions;

public class MOV implements Instr {
    private final String conditions;
    private final String dest;
    private final String src; // can be an immediate

    public MOV(String conditions, String dest, String src) {
        this.conditions = conditions;
        this.dest = dest;
        this.src = src;
    }

    @Override public String translateToArm() {
        return "MOV" + conditions + " " + dest + ", " + src;
    }
}
