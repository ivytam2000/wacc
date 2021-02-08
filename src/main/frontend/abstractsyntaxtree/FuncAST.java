package frontend.abstractsyntaxtree;

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
    Identifier t = symtab.lookupAll(returnTypeName);
    Identifier f = symtab.lookup(funcName);

    if (t == null) {
      System.out.println("Unknown type " + returnTypeName);
      return;
    }

    if (!(identifier instanceof FuncID)) {
      System.out.println(returnTypeName + " is not a type");
      return;
    }

    if (f != null) {
      System.out.println(funcName + " is already declared");
      return;
    }

    funcObj = (FuncID) identifier;
    symtab.add(funcName, funcObj);
  }
}
