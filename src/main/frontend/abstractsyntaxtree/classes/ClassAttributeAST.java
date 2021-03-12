package frontend.abstractsyntaxtree.classes;

import antlr.WaccParser.ParamContext;
import frontend.abstractsyntaxtree.Node;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class ClassAttributeAST extends Node {

  private final String varName;
  private final SymbolTable symtab;
  private final ParamContext ctx;
  private final Visibility visibility;

  public ClassAttributeAST(Identifier identifier, SymbolTable symtab,
      Visibility visibility, String varName, ParamContext ctx) {
    super(identifier);
    this.symtab = symtab;
    this.varName = varName;
    this.ctx = ctx;
    this.visibility = visibility;
  }

  @Override
  public void check() {
  }

  @Override
  public void toAssembly() {

  }
}
