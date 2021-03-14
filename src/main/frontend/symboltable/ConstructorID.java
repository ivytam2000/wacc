package frontend.symboltable;

import java.util.ArrayList;
import java.util.List;

public class ConstructorID extends Identifier {
  private final List<TypeID> params;
  private final SymbolTable symtab;
  private final Identifier identifier;
  private List<String> paramNames;

  public ConstructorID(Identifier identifier, List<TypeID> params, SymbolTable symtab) {
    this.identifier = identifier;
    this.params = params;
    this.symtab = symtab;
    this.paramNames = new ArrayList<>();
  }

  public void addParamName(String name) {
    paramNames.add(name);
  }

  public List<String> getParamNames() { return paramNames; }

  @Override
  public TypeID getType() {
    return identifier.getType();
  }

  public List<TypeID> getParams() {
    return params;
  }

  public SymbolTable getSymtab() {
    return symtab;
  }
}
