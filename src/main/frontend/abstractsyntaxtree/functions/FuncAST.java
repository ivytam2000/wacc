package frontend.abstractsyntaxtree.functions;

import antlr.WaccParser.FuncContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.Utils;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.*;

public class FuncAST extends Node {

  private final String funcName;
  private final ParamListAST params; //For backend

  private SymbolTable globalScope;
  private Node statements;

  private FuncContext ctx;

  public FuncAST(Identifier identifier, SymbolTable globalScope,
      String funcName, ParamListAST params, FuncContext ctx) {
    super(identifier);
    this.funcName = funcName;
    this.params = params;
    this.globalScope = globalScope;
    this.ctx = ctx;
  }

  public void setStatements(Node statements) {
    this.statements = statements;
  }

  @Override
  public void check() {
    // Return type of body
    TypeID bodyReturnType = Utils
        .inferFinalReturnType(statements, ctx.getStart().getLine());
    // Declared return type
    TypeID funcReturnType = identifier.getType();

    // Body can just exit and match any return type
    if (!(bodyReturnType instanceof ExitID || Utils
        .typeCompat(funcReturnType, bodyReturnType))) {
      SemanticErrorCollector.addIncompatibleType(funcReturnType.getTypeName(),
          bodyReturnType.getTypeName(), funcName, ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());
    }
  }

  public void addFuncToGlobalScope() {
    Identifier f = globalScope.lookupAll(funcName);

    //f already defined
    if (f != null) {
      SemanticErrorCollector.addSymbolAlreadyDefined(
          funcName, ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());
      return;
    }

    globalScope.add(funcName, identifier);
  }
}
