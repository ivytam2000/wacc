package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import java.util.List;

public class ParamListAST extends Node {

  protected List<ParamAST> paramASTs;

  protected ParamListAST(Identifier identifier, SymbolTable symtab, Node parent,
      List<ParamAST> paramASTs) {
    super(identifier, symtab, parent);
    this.paramASTs = paramASTs;
  }

  @Override
  public void check() {
    for (ParamAST paramAST : paramASTs) {
      paramAST.check();
    }
  }
}
