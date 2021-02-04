package frontend.symboltable;

import java.util.List;

public class FuncID extends Identifier {

  private final TypeID returnType;
  private final List<TypeID> params;

  public FuncID(TypeID returnType, List<TypeID> params) {
    this.returnType = returnType;
    this.params = params;
  }

  @Override
  public TypeID getType() {
    return returnType;
  }
}
