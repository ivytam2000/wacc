package frontend.abstractsyntaxtree;

import frontend.symboltable.Identifier;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.TypeID;

public class BaseTypeAST extends Node {

  private final String typeName;
  private TypeID baseTypeObj;

  protected BaseTypeAST(Identifier identifier, SymbolTable symtab, Node parent) {
    super(identifier, symtab, parent);
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
