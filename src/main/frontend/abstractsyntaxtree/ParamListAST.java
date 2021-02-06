package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class ParamListAST extends Node {

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
}
