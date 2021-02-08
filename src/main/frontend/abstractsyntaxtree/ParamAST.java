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

  protected ParamAST(Identifier identifier, SymbolTable symtab, String varName) {
    super(identifier);
    this.symtab = symtab;
    this.typeName = identifier.getType().getTypeName();
    this.varName = varName;
  }

  @Override
  public void check() {
    Identifier t = symtab.lookupAll(typeName);

    if (t == null) {
      SemanticErrorCollector.addError("Unknown type " + typeName);
      return;
    }

    if (!(identifier instanceof ParamID)) {
      SemanticErrorCollector.addError(typeName + " is not a type");
      return;
    }

    paramObj = (ParamID) identifier;
    symtab.add(varName, paramObj);
  }
}
