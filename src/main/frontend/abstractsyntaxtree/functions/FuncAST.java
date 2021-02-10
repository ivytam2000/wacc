package frontend.abstractsyntaxtree.functions;

import antlr.WaccParser.FuncContext;
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

  private FuncContext ctx;

  public FuncAST(Identifier identifier, SymbolTable currSymTab, String funcName,
      ParamListAST params, FuncContext ctx) {
    super(identifier);
    this.funcName = funcName;
    this.params = params;
    this.symtab = currSymTab;
    this.returnTypeName = identifier.getType().getTypeName();
    this.ctx = ctx;
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
        SemanticErrorCollector
            .addIncompatibleType(returnTypeName, returnType.getTypeName(), funcName,
                ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
      }
    }

  }

  public void checkFunctionNameAndReturnType() {
    Identifier f = symtab.lookupAll(funcName);

    if (f != null) {
      SemanticErrorCollector.addSymbolAlreadyDefined(funcName, ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());
      return;
    }

    symtab.add(funcName, identifier);
  }
}
