package frontend.abstractsyntaxtree;

import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.ParamID;
import frontend.symboltable.SymbolTable;

public class ParamAST extends Node {

  private final String typeName;
  private final String varName;
  private ParamID paramObj;
  private SymbolTable symtab;

  public ParamAST(Identifier identifier, SymbolTable symtab, String varName) {
    super(identifier);
    this.symtab = symtab;
    this.typeName = identifier.getType().getTypeName();
    this.varName = varName;
  }

  @Override
  public void check() {
    Identifier v = symtab.lookup(varName);

    if (v != null) {
      SemanticErrorCollector.addError(varName + " is already declared");
      return;
    }

    paramObj = (ParamID) identifier;
    symtab.add(varName, paramObj);
  }
}
