package frontend.symboltable;

import java.util.List;

public class FuncID extends Identifier {

  private final TypeID returnType;
  private final List<TypeID> params;
  private SymbolTable symtab;

  public FuncID(TypeID returnType, List<TypeID> params, SymbolTable symtab) {
    this.returnType = returnType;
    this.params = params;
    this.symtab = symtab;
  }

  @Override
  public TypeID getType() {
    return returnType;
  }

  public void setSymtab(SymbolTable symtab) {
    this.symtab = symtab;
  }

  public void appendParam(TypeID param) {
    params.add(param);
  }

  public List<TypeID> getParams() {
    return params;
  }
}
