package frontend.abstractsyntaxtree.functions;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.FuncID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class FuncAST extends Node {

  private final String funcName;
  private final ParamListAST params;

  private String returnTypeName;
  private SymbolTable symtab;
  private Node statements;

  public FuncAST(Identifier identifier, SymbolTable currSymTab, String funcName,
      ParamListAST params) {
    super(identifier);
    this.funcName = funcName;
    this.params = params;
    this.symtab = currSymTab;
    this.returnTypeName = identifier.getType().getTypeName();
  }

  public void setStatements(Node statements) {
    this.statements = statements;
  }

  @Override
  public void check() {

    ((FuncID) identifier).setSymtab(symtab);

    TypeID returnType = Utils.inferFinalReturnType(statements);

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
