package frontend.abstractsyntaxtree.classes;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.functions.ParamListAST;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class ClassConstructorAST extends Node {

  private final SymbolTable classScope;
  private final String className;
  private final ParamListAST paramList;

  public ClassConstructorAST(Identifier identifier, SymbolTable classScope,
      String className, ParamListAST paramList) {
    // identifier will be the classID
    super(identifier);
    this.classScope = classScope;
    this.className = className;
    this.paramList = paramList;
  }

  @Override
  public void check() {
    // TODO check if function is defined in the outer scope, same name as the className
    // need to check if the attributes are all initialised
  }

  @Override
  public void toAssembly() {

  }
}
