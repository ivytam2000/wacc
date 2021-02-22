package frontend.abstractsyntaxtree.functions;

import antlr.WaccParser.ParamContext;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;

public class ParamAST extends Node {

  private final String varName;
  private final SymbolTable symtab;
  private final ParamContext ctx;

  public ParamAST(Identifier identifier, SymbolTable symtab, String varName,
      ParamContext ctx) {
    super(identifier);
    this.symtab = symtab;
    this.varName = varName;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    Identifier v = symtab.lookup(varName);
    if (v != null) {
      SemanticErrorCollector.addSymbolAlreadyDefined(
          varName, ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());
      return;
    }
    symtab.add(varName, identifier);
  }
}
