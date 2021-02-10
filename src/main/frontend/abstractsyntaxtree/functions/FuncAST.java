package frontend.abstractsyntaxtree.functions;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Parent;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class FuncAST extends Node {

  private final String funcName;
  private final ParamListAST params;
  private final SymbolTable globalScope;
  private final Node statAST;

  private FuncID funcObj;

  public FuncAST(Identifier identifier, SymbolTable globalScope,
      String funcName,
      ParamListAST params, Node statAST) {
    super(identifier);
    this.globalScope = globalScope;
    this.funcName = funcName;
    this.params = params;
    this.statAST = statAST;
  }

  @Override
  public void check() {
    checkFunctionNameAndReturnType();

    for (ParamAST paramAST : params.paramASTs) {
      paramAST.check();
      funcObj.appendParam(paramAST.getIdentifier().getType());
    }
  }

  public void checkFunctionNameAndReturnType() {
    Identifier f = globalScope.lookup(funcName);

    if (f != null) {
      SemanticErrorCollector.addError(funcName + " is already declared");
      return;
    }

    funcObj = (FuncID) identifier;
    globalScope.add(funcName, funcObj);
  }
}
