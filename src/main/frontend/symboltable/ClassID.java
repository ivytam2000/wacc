package frontend.symboltable;


public class ClassID extends TypeID {

  private SymbolTable symtab;

  public ClassID(String className, SymbolTable symtab) {
    super(className);
    this.symtab = symtab;
  }

  @Override
  public TypeID getType() {
    return this;
  }

  public void setSymtab(SymbolTable symtab) {
    this.symtab = symtab;
  }

  public SymbolTable getSymtab() {
    return symtab;
  }
}
