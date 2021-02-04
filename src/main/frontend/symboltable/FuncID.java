package frontend.symboltable;

import java.util.List;

public class FuncID extends Identifier {

  private final TypeID type;
  private final List<TypeID> params;
  private final SymbolTable symtab;


  public FuncID(TypeID type, List<TypeID> params, SymbolTable parent) {
    this.type = type;
    this.params = params;
    this.symtab = new SymbolTable(parent);
  }

  @Override
  public TypeID getType() {
    return type;
  }
}
