package frontend.abstractsyntaxtree.classes;

import antlr.WaccParser.ClassFuncContext;
import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.functions.FuncAST;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.ConstructorID;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class ClassFuncAST extends Node {

  private final SymbolTable classScope;
  private String className;
  private final Visibility visibility;
  private final FuncAST funcAST;
  private final ClassFuncContext ctx;


  public ClassFuncAST(Identifier identifier, SymbolTable classScope,
      Visibility visibility, FuncAST funcAST, ClassFuncContext ctx) {
    super(identifier);
    identifier.setVisibility(visibility);
    this.funcAST = funcAST;
    this.visibility = visibility;
    this.classScope = classScope;
    this.ctx = ctx;
  }

  // might not need
  public void setClassName(String className) {
    this.className = className;
  }

  @Override
  public void check() {
    // check if function exists and doesn't have same name as constructor
    String funcName = funcAST.getFuncName();
    Identifier func = classScope.lookup(funcName);

    if (func != null) {
      if (func instanceof ConstructorID) {
        SemanticErrorCollector.addFunctionHasSameNameAsClassConstructor(
            funcName, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
      } else {
        SemanticErrorCollector.addSymbolAlreadyDefined(
            funcName, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
      }
    }

    // checks that the function has correct return type with body
    funcAST.check();

  }

  @Override
  public void toAssembly() {

  }
}
