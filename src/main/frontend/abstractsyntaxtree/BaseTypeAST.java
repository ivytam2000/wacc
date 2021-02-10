package frontend.abstractsyntaxtree;

import antlr.WaccParser.BaseTypeContext;
import frontend.errorlistener.SemanticErrorCollector;
import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class BaseTypeAST extends Node {

  private final String typeName;
  private SymbolTable symtab;
  private BaseTypeContext ctx;

  public BaseTypeAST(Identifier identifier, SymbolTable symtab, BaseTypeContext ctx) {
    super(identifier);
    this.symtab = symtab;
    this.typeName = identifier.getType().getTypeName();
    this.ctx = ctx;
  }

  @Override
  public void check() {
    Identifier t = symtab.lookupAll(typeName);

    if (t == null) {
      SemanticErrorCollector.addUnknownType(typeName, ctx.getStart().getLine(),
          ctx.getStart().getCharPositionInLine());
    }
  }
}
