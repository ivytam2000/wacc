package frontend.symboltable;

public class StringID extends TypeID {

    public StringID() {
        super("string");
    }

    @Override public TypeID getType() {
        return this;
    }

}
