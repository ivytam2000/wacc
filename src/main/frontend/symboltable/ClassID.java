package frontend.symboltable;

import java.util.List;

public class ClassID extends TypeID {

  private SymbolTable symtab;

  public ClassID(String className, SymbolTable symtab) {
    super(className);
  }

  @Override
  public TypeID getType() {
    return this;
  }

  @Override public int getBytes() {return 4;}

  public void setSymtab(SymbolTable symtab) {
    this.symtab = symtab;
  }

  public SymbolTable getSymtab() {
    return symtab;
  }
}
