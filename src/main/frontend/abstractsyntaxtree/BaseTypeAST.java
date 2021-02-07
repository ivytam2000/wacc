package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class BaseTypeAST extends Node {

  private final String typeName;
  private TypeID baseTypeObj;
  private SymbolTable symtab;

  public BaseTypeAST(Identifier identifier, SymbolTable symtab) {
    super(identifier);
    this.symtab = symtab;
    this.typeName = identifier.getType().getTypeName();
  }

  @Override
  public void check() {
    Identifier t = symtab.lookupAll(typeName);

    if (t == null) {
      System.out.println("Unknown type " + typeName);
      return;
    }

    baseTypeObj = (TypeID) identifier;
  }
}
