package frontend.symboltable;

public class ArrayID extends TypeID {

    private final TypeID type;
    private final int size;

    public ArrayID(TypeID type, int size) {
        super("array (" + type.getTypeName() + ")");
        this.type = type;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public TypeID getElemType(){
        return type;
    }

    @Override public TypeID getType() {
        return this;
    }

}
