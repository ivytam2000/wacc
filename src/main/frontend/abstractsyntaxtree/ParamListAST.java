package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;
import java.util.List;
import java.util.stream.Collectors;

public class ParamListAST extends Parent {

  protected List<ParamAST> paramASTs;
  private SymbolTable symtab;

  protected ParamListAST(Identifier identifier, SymbolTable symtab, List<ParamAST> paramASTs) {
    super(identifier);
    this.symtab = symtab;
    this.paramASTs = paramASTs;
  }

  @Override
  public void check() {
    for (ParamAST paramAST : paramASTs) {
      paramAST.check();
    }
  }

  public List<TypeID> convertToParamIDs() {
    return paramASTs.stream().map(paramAST -> paramAST.getIdentifier().getType())
        .collect(Collectors.toList());
  }
}
