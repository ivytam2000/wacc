package frontend.abstractsyntaxtree.classes;

import frontend.abstractsyntaxtree.Node;
import frontend.abstractsyntaxtree.functions.FuncAST;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class ClassFuncAST extends Node {

  private SymbolTable classScope;
  private String className;
  private Visibility visibility;
  private FuncAST funcAST;


  public ClassFuncAST(Identifier identifier, SymbolTable classScope,
      Visibility visibility, FuncAST funcAST) {
    super(identifier);
    this.funcAST = funcAST;
    this.visibility = visibility;
    this.classScope = classScope;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  @Override
  public void check() {
    // TODO need to check if function exists, cant have same name as constructor
  }

  @Override
  public void toAssembly() {

  }
}
