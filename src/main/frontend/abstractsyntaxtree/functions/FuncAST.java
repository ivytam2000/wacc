package frontend.abstractsyntaxtree.functions;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Parent;
import frontend.abstractsyntaxtree.Utils;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class FuncAST extends Parent {

  private String returnTypeName;
  private String funcName;
  private ParamListAST params;
  private SymbolTable symtab;
  private Node statements;

  public FuncAST(Identifier identifier, SymbolTable currSymTab, String funcName,
      ParamListAST params, Node statements) {
    super(identifier);
    this.symtab = currSymTab;
    this.returnTypeName = identifier.getType().getTypeName();
    this.funcName = funcName;
    this.params = params;
    this.statements = statements;
  }

  @Override
  public void check() {
    checkFunctionNameAndReturnType();

    ((FuncID) identifier).setSymtab(symtab);

    TypeID returnType = Utils.getReturnType(statements);

    if (returnType != null) {
      if (!returnType.getTypeName().equals(returnTypeName)) {
        SemanticErrorCollector.addError(funcName + " expected return type " + returnTypeName + " but got " + returnType.getTypeName());
      }
    }

  }

  public void checkFunctionNameAndReturnType() {
    Identifier f = symtab.lookupAll(funcName);

    if (f != null) {
      SemanticErrorCollector.addError(funcName + " is already declared");
      return;
    }

    symtab.add(funcName, identifier);
  }
}
