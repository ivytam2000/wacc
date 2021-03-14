package frontend.symboltable;


public class ClassID extends TypeID {

  private final SymbolTable symtab;
  private final int attributeBytes;

  public ClassID(String className, SymbolTable symtab, int attributeBytes) {
    super(className);
    this.symtab = symtab;
    this.attributeBytes = attributeBytes;
  }

  @Override
  public TypeID getType() {
    return this;
  }

  @Override public int getBytes() {return attributeBytes;}

  public SymbolTable getSymtab() {
    return symtab;
  }
}
