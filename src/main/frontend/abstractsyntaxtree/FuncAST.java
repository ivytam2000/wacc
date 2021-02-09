package frontend.abstractsyntaxtree;

import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.ParamID;
import frontend.symboltable.SymbolTable;

public class FuncAST extends Parent {

  private String returnTypeName;
  private String funcName;
  private ParamListAST params;
  private FuncID funcObj;
  private SymbolTable symtab;

  public FuncAST(Identifier identifier, SymbolTable currSymTab, String funcName,
      ParamListAST params) {
    super(identifier);
    this.symtab = currSymTab;
    this.returnTypeName = identifier.getType().getTypeName();
    this.funcName = funcName;
    this.params = params;
  }

  @Override
  public void check() {
    checkFunctionNameAndReturnType();

    funcObj.setSymtab(symtab);

    for (ParamAST paramAST : params.paramASTs) {
      paramAST.check();
      funcObj.appendParam(paramAST.getIdentifier().getType());
    }
  }

  public void checkFunctionNameAndReturnType() {
    Identifier f = symtab.lookup(funcName);

    if (f != null) {
      SemanticErrorCollector.addError(funcName + " is already declared");
      return;
    }

    funcObj = (FuncID) identifier;
    symtab.add(funcName, funcObj);
  }
}
