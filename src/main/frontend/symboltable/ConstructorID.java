package frontend.symboltable;

import java.util.List;

public class ConstructorID extends Identifier {
  private final List<TypeID> params;
  private SymbolTable symtab;
  private Identifier identifier;

  public ConstructorID(Identifier identifier, List<TypeID> params, SymbolTable symtab) {
    this.identifier = identifier;
    this.params = params;
    this.symtab = symtab;
  }

  @Override
  public TypeID getType() {
    return identifier.getType();
  }

  public void setSymtab(SymbolTable symtab) {
    this.symtab = symtab;
  }

  public List<TypeID> getParams() {
    return params;
  }

  public SymbolTable getSymtab() {
    return symtab;
  }
}
