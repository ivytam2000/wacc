package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class PairElemTypeAST extends Node {

  private final String typeName;
  private TypeID pairElemTypeObj;
  private SymbolTable symtab;

  public PairElemTypeAST(Identifier identifier, SymbolTable symtab) {
    super(identifier);
    this.symtab = symtab;
    this.typeName = identifier.getType().getTypeName();
  }

  @Override
  public void check() {
    Identifier t = symtab.lookupAll(typeName);

    if (t == null) {
      symtab.add(typeName, identifier);
    }

    pairElemTypeObj = (TypeID) identifier;
  }
}