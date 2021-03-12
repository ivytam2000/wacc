package frontend.abstractsyntaxtree.classes;

import frontend.abstractsyntaxtree.assignments.AssignRHSAST;
import frontend.abstractsyntaxtree.functions.ArgListAST;
import frontend.symboltable.SymbolTable;

public class ClassInstantAST extends AssignRHSAST {

  private final String className;
  private final ArgListAST args;
  private final SymbolTable symtab;

  public ClassInstantAST(String className, SymbolTable symtab, ArgListAST args) {
    super(symtab.lookupAll("class " + className), symtab);
    this.className = className;
    this.args = args;
    this.symtab = symtab;
  }

  @Override
  public void check() {
    // TODO need to do
  }

  @Override
  public void toAssembly() {

  }
}
