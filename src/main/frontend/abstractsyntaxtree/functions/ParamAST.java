package frontend.abstractsyntaxtree.functions;

import antlr.WaccParser.ParamContext;
import frontend.abstractsyntaxtree.Node;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.ParamID;
import frontend.symboltable.SymbolTable;

public class ParamAST extends Node {

  private final String typeName;
  private final String varName;
  private ParamID paramObj;
  private SymbolTable symtab;
  private ParamContext ctx;

  public ParamAST(Identifier identifier, SymbolTable symtab, String varName, ParamContext ctx) {
    super(identifier);
    this.symtab = symtab;
    this.typeName = identifier.getType().getTypeName();
    this.varName = varName;
    this.ctx = ctx;
  }

  @Override
  public void check() {
    Identifier v = symtab.lookup(varName);
    if (v != null) {
      SemanticErrorCollector.addSymbolAlreadyDefined(
          varName, ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
      return;
    }
    paramObj = (ParamID) identifier;
    symtab.add(varName, paramObj);
  }
}
