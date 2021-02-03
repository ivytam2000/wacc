package frontend.symboltable;

public class StringID extends TypeID {
    //TODO: Is it useful to add length?

    public StringID() {
        super("string");
    }

    @Override public TypeID getType() {
        return this;
    }

}
